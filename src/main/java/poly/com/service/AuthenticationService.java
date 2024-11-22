package poly.com.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import poly.com.Enum.*;
import poly.com.configuration.JwtService;
import poly.com.dto.request.Auth.*;
import poly.com.dto.response.Auth.AuthenticationResponse;
import poly.com.exception.UserNotFoundException;
import poly.com.model.*;
import poly.com.repository.*;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final VerifyRepository verifyRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenRepository tokenRepository;
    private final CompanyRepository companyRepository;

    @Value("${spring.mailing.frontend.activation-url}")
    private String activationUrl;

    /**
     * Đăng ký tài khoản người dùng mới với vai trò là User
     */
    public AuthenticationResponse registerUser(AuthRegisterRequest request) throws MessagingException, IllegalAccessException {
        validateEmailAndPassword(request.getEmail(), request.getPassword(), request.getIsPassword());

        User user = createUser(request.getFirstname(), request.getLastname(), request.getEmail(), request.getPassword(), RoleType.ROLE_USER);
        userRepository.save(user);
        sendValidationEmail(user);

        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, jwtToken);

        return createAuthResponse(jwtToken, refreshToken);
    }

    /**
     * Đăng ký tài khoản công ty mới với vai trò là Company
     */
    public AuthenticationResponse registerCompany(RegisterCompanyRequest request) throws MessagingException, IllegalAccessException {
        validateEmailAndPassword(request.getEmail(), request.getPassword(), request.getIsPassword());

        User user = createUser(request.getFirstname(), request.getLastname(), request.getEmail(), request.getPassword(), RoleType.ROLE_COMPANY);
        User savedUser = userRepository.save(user);

        Company company = Company.builder()
         .name(request.getCompanyName())
         .city(request.getCity())
         .district(request.getDistrict())
         .user(savedUser)
         .phone(request.getCompanyPhone())
         .status(StatusEnum.PENDING)
         .build();
        companyRepository.save(company);

        sendValidationEmail(user);

        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, jwtToken);

        return createAuthResponse(jwtToken, refreshToken);
    }

    /**
     * Tạo đối tượng User từ thông tin đăng ký
     */
    private User createUser(String firstname, String lastname, String email, String password, RoleType roleType) throws IllegalAccessException {
        var role = roleRepository.findByRole(roleType)
         .orElseThrow(() -> new IllegalAccessException("Role not found"));

        return User.builder()
         .firstname(firstname)
         .lastname(lastname)
         .email(email)
         .password(passwordEncoder.encode(password))
         .provider(ProviderEnum.valueOf("LOCAL"))
         .accountLocked(false)
         .enabled(false)
         .roles(List.of(role))
         .build();
    }

    /**
     * Gửi mã xác thực đến email của người dùng
     */
    private void sendValidationEmail(User user) throws MessagingException {
        String generatedToken = generateAndSaveActivationToken(user);
        emailService.sendEmail(
         user.getEmail(),
         user.getFullName(),
         EmailTemplate.ACTIVATION_ACCOUNT,
         activationUrl,
         generatedToken,
         "Activate your account"
        );
    }

    /**
     * Tạo và lưu mã xác thực tài khoản với thời gian hết hạn 10 phút
     */
    private String generateAndSaveActivationToken(User user) {
        String generatedToken = generateActivationCode(6);
        VerifyUser token = VerifyUser.builder()
         .token(generatedToken)
         .createdAt(LocalDateTime.now())
         .expiredAt(LocalDateTime.now().plusMinutes(10))
         .user(user)
         .build();
        verifyRepository.save(token);
        return generatedToken;
    }

    /**
     * Tạo mã xác thực ngẫu nhiên
     */
    private String generateActivationCode(int codeLength) {
        String chars = "0123456789";
        StringBuilder code = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for (int i = 0; i < codeLength; i++) {
            int randomIndex = secureRandom.nextInt(chars.length());
            code.append(chars.charAt(randomIndex));
        }
        return code.toString();
    }

    /**
     * Xác thực người dùng
     */
    @Transactional
    public AuthenticationResponse authenticate(LoginRequest request, HttpServletRequest httpRequest)
     throws AuthenticationFailedException {
        try {
            // Xác thực người dùng
            Authentication authentication = authenticationManager.authenticate(
             new UsernamePasswordAuthenticationToken(
              request.getEmail(),
              request.getPassword()
             )
            );

            // Lấy thông tin người dùng
            User user = userRepository.findByEmail(request.getEmail())
             .orElseThrow(() -> new UserNotFoundException("Không tìm thấy người dùng"));

            // Kiểm tra trạng thái tài khoản
            if (!user.isEnabled()) {
                throw new AuthenticationFailedException("Tài khoản chưa được kích hoạt");
            }

            if (user.isAccountLocked()) {
                throw new AuthenticationFailedException("Tài khoản đã bị khóa");
            }

            // Lấy danh sách roles
            List<RoleType> userRoles = user.getRoles().stream()
             .map(Role::getRole)
             .collect(Collectors.toList());

            // Đặt authentication vào SecurityContext
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Lưu thông tin vào session
            HttpSession session = httpRequest.getSession(true); // Tạo session mới nếu chưa tồn tại
            session.setMaxInactiveInterval(30 * 60); // Set thời gian timeout session (30 phút)

            // Lưu thông tin cần thiết vào session
            session.setAttribute("user", user);
            session.setAttribute("userId", user.getId());
            session.setAttribute("userEmail", user.getEmail());
            session.setAttribute("userRoles", userRoles);
            session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

            // Kiểm tra role và set URL chuyển hướng
            String redirectUrl = determineRedirectUrl(userRoles);

            // Xây dựng response
            return AuthenticationResponse.builder()
             .id(user.getId())
             .email(user.getEmail())
             .fullName(user.getFullName())
             .roles(userRoles)
             .redirectUrl(redirectUrl) // Thêm URL chuyển hướng vào response
             .build();

        } catch (AuthenticationException e) {
            log.error("Đăng nhập thất bại: {}", e.getMessage(), e);
            throw new AuthenticationFailedException("Email hoặc mật khẩu không chính xác");
        }
    }

    // Phương thức hỗ trợ xác định URL chuyển hướng dựa trên role
    private String determineRedirectUrl(List<RoleType> roles) {
        if (roles.contains(RoleType.ROLE_COMPANY)) {
            return "/JobPost";
        } else if (roles.contains(RoleType.ROLE_ADMIN)) {
            return "/admin/dashboard";
        } else if (roles.contains(RoleType.ROLE_USER)) {
            return "/user/dashboard";
        }
        return "/home"; // URL mặc định
    }



/**
     * Thay đổi mật khẩu của người dùng đã đăng nhập
     */
    public AuthenticationResponse changePassword(UserChangepasswordDTO request) {
        User user = getAuthenticatedUser();
        validateOldPassword(request.getOldPassword(), user);
        validateNewPassword(request.getNewPassword(), request.getConfirmPassword());

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        revokeUserToken(user);
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, jwtToken);
        return createAuthResponse(jwtToken, refreshToken);
    }

    /**
     * Làm mới mật khẩu và gửi qua email
     */
    public AuthenticationResponse forgotPassword(String email) throws MessagingException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));
        String newPassword = generateActivationCode(8);

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        emailService.sendEmail(
         user.getEmail(),
         user.getFullName(),
         EmailTemplate.FORGOT_PASSWORD,
         null,
         newPassword,
         "Your new password"
        );

        revokeUserToken(user);
        String jwtToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        saveUserToken(user, jwtToken);
        return createAuthResponse(jwtToken, refreshToken);
    }

    /**
     * Thu hồi token của người dùng
     */
    private void revokeUserToken(User user) {
        var validTokens = tokenRepository.findAllValidByUserId(user.getId());
        if (!validTokens.isEmpty()) {
            validTokens.forEach(token -> {
                token.setRevoked(true);
                token.setExpired(true);
            });
            tokenRepository.saveAll(validTokens);
        }
    }

    /**
     * Lưu token của người dùng
     */
    private void saveUserToken(User user, String jwtToken) {
        Token token = Token.builder()
         .user(user)
         .token(jwtToken)
         .tokenType(TokenType.BEARER)
         .revoked(false)
         .expired(false)
         .build();
        tokenRepository.save(token);
    }

    /**
     * Kích hoạt tài khoản người dùng bằng mã xác thực
     */
    @Transactional
    public void activateAccount(String verifyCode) throws MessagingException {
        VerifyUser  savedVerifyUser  = verifyRepository.findByToken(verifyCode)
                .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (LocalDateTime.now().isAfter(savedVerifyUser .getExpiredAt())) {
            sendValidationEmail(savedVerifyUser .getUser ());
            throw new IllegalArgumentException("Activation token has expired. A new token has been sent to the same email address");
        }

        User user = userRepository.findById(savedVerifyUser .getUser ().getId())
                .orElseThrow(() -> new UserNotFoundException("User  not found"));
        System.out.println(user.isEnabled());
        // Kiểm tra xem người dùng có bị vô hiệu hóa không
        if (!user.isEnabled()) {
            // Nếu bạn muốn kích hoạt tài khoản cho người dùng đã bị vô hiệu hóa
            user.setEnabled(true); // Kích hoạt tài khoản
            System.out.println(user.isEnabled());
        }

        // Cập nhật thông tin người dùng
        userRepository.save(user);
        savedVerifyUser .setValidateAt(LocalDateTime.now());
        verifyRepository.save(savedVerifyUser );
    }

    /**
     * Gửi lại mã xác thực qua email
     */
    public void resendVerificationCode(String email) throws MessagingException {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.isEnabled()) {
            throw new RuntimeException("Account is already verified");
        }

        String newVerificationCode = generateActivationCode(6);
        VerifyUser verifyUser = verifyRepository.findByUser(user)
         .orElseThrow(() -> new UserNotFoundException("Token not found for user"));

        verifyUser.setToken(newVerificationCode);
        verifyUser.setCreatedAt(LocalDateTime.now());
        verifyUser.setExpiredAt(LocalDateTime.now().plusMinutes(15));
        verifyRepository.save(verifyUser);
        sendValidationEmail(user);
    }

    /**
     * Làm mới token
     */
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return;
        }

        final String refreshToken = authorizationHeader.substring(7);
        final String userEmail = jwtService.extractUsername(refreshToken);

        if (userEmail != null) {
            User userDetails = userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException("User not found"));
            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                String accessToken = jwtService.generateToken(userDetails);
                revokeUserToken(userDetails);
                saveUserToken(userDetails, accessToken);
                AuthenticationResponse authResponse = createAuthResponse(accessToken, refreshToken);
                new ObjectMapper().writeValue(response.getOutputStream(), authResponse);
            }
        }
    }

    // Phương thức phụ trợ để tạo phản hồi xác thực
    private AuthenticationResponse createAuthResponse(String accessToken, String refreshToken) {
        return AuthenticationResponse.builder()
         .accessToken(accessToken)
         .refreshToken(refreshToken)
         .build();
    }

    // Phương thức phụ trợ để xác nhận email và mật khẩu
    private void validateEmailAndPassword(String email, String password, String confirmPassword) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already in use");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Password and confirm password do not match");
        }
    }

    // Lấy thông tin người dùng đã xác thực
    private User getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    // Kiểm tra mật khẩu cũ
    private void validateOldPassword(String oldPassword, User user) {
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Old password is incorrect");
        }
    }

    // Kiểm tra mật khẩu mới và xác nhận
    private void validateNewPassword(String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new RuntimeException("New password and confirm password do not match");
        }
    }
}