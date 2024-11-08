package poly.com.service;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import poly.com.Enum.EmailTemplate;
import poly.com.Enum.RoleType;
import poly.com.configuration.JwtService;
import poly.com.dto.AuthRegisterDTO;
import poly.com.dto.AuthenticationDTO;
import poly.com.dto.response.AuthenResponse;
import poly.com.exception.UserNotFoundException;
import poly.com.model.Token;
import poly.com.model.User2;
import poly.com.repository.RoleRepository;
import poly.com.repository.TokenRepository;
import poly.com.repository.User2Repository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final User2Repository user2Repository;
    private final TokenRepository tokenRepository;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${spring.mailing.frontend.activation-url}")
    private String activationUrl;


    /*
     * @author: VuDD
     * @since: 11/7/2024 11:16 PM
     * @description:  Đăng ký tài khoản
     * @update:
     *
     * */
    public void register( AuthRegisterDTO authRegisterDTO) throws IllegalAccessException, MessagingException {
        if (user2Repository.existsByEmail(authRegisterDTO.getEmail())) {
            throw new IllegalArgumentException("Email already in use");
        }
        var Role = roleRepository.findByRole(RoleType.ROLE_USER)
        .orElseThrow(() -> new IllegalAccessException("Role not found"));

        var user = User2.builder()
        .firstname(authRegisterDTO.getFirstname())
        .lastname(authRegisterDTO.getLastname())
        .email(authRegisterDTO.getEmail())
        .password(passwordEncoder.encode(authRegisterDTO.getPassword()))
        .accountLocked(false)
        .enabled(false)
        .roles(List.of(Role))
        .build();

        user2Repository.save(user);
        sendValidationEmail(user);
    }


    /*
     * @author: VuDD
     * @since: 11/7/2024 11:17 PM
     * @description:  gửi mã xác thực qua email
     * @update:
     *
     * */
    private void sendValidationEmail(User2 user) throws MessagingException {
        String generatedToken = generateAndSaveActicationToken(user);
        emailService.sendEmail(
        user.getEmail(),
        user.getFullName(),
        EmailTemplate.ACTICATION_ACCOUNT,
        activationUrl,
        generatedToken,
        "Activate your account"
        );
    }

    /*
     * @author: VuDD
     * @since: 11/7/2024 11:11 PM
     * @description: generateAndSaveActicationToken: Tạo và lưu mã xác thực tài khoản 10p die mã
     * @update:
     *
     * */
    private String generateAndSaveActicationToken(User2 user) {
        // Generate token
        String generatedToken = generateActivationCode(6);
        var token = Token.builder()
        .token(generatedToken)
        .createdAt(LocalDateTime.now())
        .expiredAt(LocalDateTime.now().plusMinutes(10)) // Thời gian hết hạn 10 phút
        .user(user)
        .build();
        tokenRepository.save(token);
        return generatedToken;
    }


    /*
     * @author: VuDD
     * @since: 11/7/2024 11:12 PM
     * @description: generateActivationCode: Tạo mã xác thực
     * @update:
     *
     * */
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


    /*
     * @author: VuDD
     * @since: 11/7/2024 11:12 PM
     * @description:  login: Đăng nhập hệ thống
     * @update:
     *
     * */
    @Transactional
    public AuthenResponse authenticate(AuthenticationDTO request) {
        var auth = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(
        request.getEmail(),
        request.getPassword()
        )
        );
        var claims = new HashMap<String, Object>();
        var user = ((User2) auth.getPrincipal());
        claims.put("id", user.getId());
        claims.put("fullName", user.getFullName());
        var jwtToken = jwtService.generateToken(claims, (User2) auth.getPrincipal());
        return AuthenResponse.builder()
        .token(jwtToken)
        .build();
    }


    /*
     * @author: VuDD
     * @since: 11/7/2024 11:14 PM
     * @description:  authenticate: Kích hoạt tài khoản người dùng bằng mã xác thực
     * @update:
     *
     * */
    @Transactional
    public void activateAccount(String token) throws MessagingException {
        Token savedToken = tokenRepository.findByToken(token)
        .orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (LocalDateTime.now().isAfter(savedToken.getExpiredAt())) {
            sendValidationEmail(savedToken.getUser());
            throw new IllegalArgumentException("Activation token has expired. A new token has been sent to the same email address");
        }

        User2 user = user2Repository.findById(savedToken.getUser().getId())
        .orElseThrow(() -> new UserNotFoundException("User not found"));

        user.setEnabled(true);
        user2Repository.save(user);
        savedToken.setValidateAt(LocalDateTime.now());
        tokenRepository.save(savedToken);
    }


    /*
     * @author: VuDD
     * @since: 11/7/2024 11:16 PM
     * @description:  Gưi lại mã xác thực theo email
     * @update:
     *
     * */
    public void resendVerificationCode(String email) throws MessagingException {
        // Tìm người dùng theo email
        Optional<User2> optionalUser = user2Repository.findByEmail(email);
        if (optionalUser.isPresent()) {
            User2 user = optionalUser.get();
            if (user.isEnabled()) {
                throw new RuntimeException("Account is already verified");
            }

            // Tạo mã xác thực mới và cập nhật thông tin người dùng
            String newVerificationCode = generateActivationCode(6);
            Token token = tokenRepository.findByUser(user)
            .orElseThrow(() -> new UserNotFoundException("Token not found for user"));
            token.setToken(newVerificationCode);
            token.setExpiredAt(LocalDateTime.now().plusMinutes(15)); // Thay đổi thời gian hết hạn nếu cần
            tokenRepository.save(token);

            // Gửi email xác thực mới
            sendValidationEmail(token.getUser());
        } else {
            throw new UserNotFoundException("User not found");
        }
    }
}
