package poly.com.controller;

import com.microsoft.sqlserver.jdbc.StringUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.com.Enum.RoleType;
import poly.com.dto.request.Auth.AuthRegisterRequest;
import poly.com.dto.request.Auth.LoginRequest;
import poly.com.dto.request.Auth.RegisterCompanyRequest;
import poly.com.dto.request.Auth.UserChangepasswordDTO;
import poly.com.dto.response.Auth.AuthenticationResponse;
import poly.com.exception.ApiResponse;
import poly.com.model.User;
import poly.com.service.AuthenticationService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.http.ResponseEntity.status;
import static poly.com.Enum.RoleType.*;

@Tag(name = "Authentication Controller")
@Controller
@SessionAttributes("currentUser ")
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final poly.com.util.AuthenticationUtil authenticationUtil;

    @PostMapping("/user")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String authUser (@Valid @ModelAttribute AuthRegisterRequest authRegisterRequest)
            throws IllegalAccessException, MessagingException {
        System.out.println("Password: " + authRegisterRequest.getPassword());
        System.out.println("Confirm Password: " + authRegisterRequest.getIsPassword());
        var result = authenticationService.registerUser (authRegisterRequest);
        return "user/authentication"; // Trả về trang xác nhận đăng ký
    }

    @GetMapping("/register/user")
    @ResponseStatus(HttpStatus.OK) // Trả về mã trạng thái 200 OK
    public String createUser () {
        return "user/register"; // Đảm bảo rằng bạn có file register.html trong thư mục templates
    }

   @PostMapping("/register/company")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public String createCompany(@Valid @RequestBody RegisterCompanyRequest request) throws IllegalAccessException,
    MessagingException {
       System.out.println("Password: " + request.getPassword());
       System.out.println("Confirm Password: " + request.getIsPassword());
        authenticationService.registerCompany(request);
       return "user/authentication"; // Trả về trang xác nhận đăng ký
    }

//    @PostMapping("/register/company")
//    @ResponseStatus(HttpStatus.ACCEPTED)
//    public ApiResponse<AuthenticationResponse> createCompany(@Valid @RequestBody RegisterCompanyRequest request) throws IllegalAccessException,
//            MessagingException {
//        //authenticationService.registerCompany(request);
//        return ApiResponse.<AuthenticationResponse>builder()
//                .status(HttpStatus.OK.value())
//                .message("Company registered successfully")
//                .Result(authenticationService.registerCompany(request))
//                .build();
//    }

    @GetMapping("login")
    @ResponseStatus(HttpStatus.OK)
    public String login (){
        return "user/login";
    }

    @GetMapping("/user/dashboard")
    public String dashboard(HttpSession session) {
        UserDetails userDetails = (UserDetails) session.getAttribute("user");
        if (userDetails != null) {
            // Người dùng đã đăng nhập, trả về trang dashboard
            return "dashboard"; // Tên của template Thymeleaf
        } else {
            // Người dùng chưa đăng nhập, chuyển hướng đến trang đăng nhập
            return "redirect:/auth/login";
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate(@RequestParam String email, @RequestParam String password, HttpSession session) {
        LoginRequest loginRequest = new LoginRequest(email, password); // Tạo đối tượng LoginRequest từ các tham số

        AuthenticationResponse authResponse = authenticationService.authenticate(loginRequest);

        // Kiểm tra xem xác thực có thành công không
        if (authResponse != null) {
            // Lấy thông tin người dùng từ authResponse
            UserDetails userDetails = authResponse.getUserDetails();
            // Lưu thông tin người dùng vào session
            session.setAttribute("user", userDetails); // Lưu thông tin người dùng vào session

            // Tạo một đối tượng JSON để trả về thông tin người dùng
            Map<String, Object> responseMap = new HashMap<>();
            responseMap.put("email", userDetails.getUsername());
            responseMap.put("authorities", userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));

            // Trả về thông tin người dùng
            return ResponseEntity.ok(responseMap);
        } else {
            // Nếu xác thực không thành công, trả về mã lỗi
            return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED).body("Authentication failed");
        }
    }

    @GetMapping("/company/vertical")
    @ResponseStatus(HttpStatus.OK)
    public String authCompany (){
        return "user/authentication";
    }

    @GetMapping("/company/dashboard")
    @ResponseStatus(HttpStatus.OK)
    public String CompanyDashboard (){
        return "recruiter/index";
    }

//    @PostMapping("/authenticate")
//    public String authenticate(
//            @RequestParam String email,
//            @RequestParam String password,
//            Model model,
//            RedirectAttributes redirectAttributes
//    ) {
//        try {
//            // Validate email và password
//            System.out.println("Email: " + email);
//            if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
//                redirectAttributes.addFlashAttribute("error", "Email và mật khẩu không được để trống");
//                return "redirect:/auth/login";
//            }
//
//            // Gọi phương thức xác thực
//            User authResponse = authenticationUtil.getAuthenticatedUser();
//
//            // Lưu thông tin người dùng vào model (sẽ được lưu trong session)
//            model.addAttribute("currentUser ", authResponse);
//
//            // Phân quyền điều hướng dựa trên vai trò
//            if (authResponse.getRoles().contains("ROLE_COMPANY")) {
//                return "redirect:/company/dashboard";
//            } else if (authResponse.getRoles().contains("ROLE_USER")) {
//                return "redirect:/auth/dashboard";
//            } else if (authResponse.getRoles().contains("ROLE_ADMIN")) {
//                return "redirect:/admin/dashboard";
//            } else {
//                redirectAttributes.addFlashAttribute("error", "Vai trò không hợp lệ");
//                return "redirect:/login";
//            }
//        } catch (BadCredentialsException e) {
//            // Xử lý ngoại lệ xác thực
//            redirectAttributes.addFlashAttribute("error", "Đăng nhập thất bại: " + e.getMessage());
//            return "redirect:/login";
//        } catch (Exception e) {
//            // Ghi log lỗi
//            e.printStackTrace();
//            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại.");
//            return "redirect:/auth/login";
//        }
//    }

    @GetMapping("/dashboard")
    @ResponseStatus(HttpStatus.OK)
    public String dashboard (Model model) {
        return "user/dashboard";
    }

    @PostMapping("/refresh-token")
    public void  refreshToken(HttpServletRequest request,
                              HttpServletResponse response) throws IOException {
        authenticationService.refreshToken(request, response);
    }
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/activate-account")
    public String activateAccountUser (@RequestParam String token, Model model) {
        if (token == null || token.isEmpty()) {
            model.addAttribute("error", "Token cannot be null or empty.");
            return "error"; // Trả về trang lỗi
        }

        try {
            authenticationService.activateAccount(token);
            System.out.println("Activated account successfully");
            return "user/login"; // Chuyển hướng đến trang đăng nhập
        } catch (Exception e) {
            logger.error("Error activating account: {}", e.getMessage());
            model.addAttribute("error", "An error occurred while activating the account.");
            return "error"; // Trả về trang lỗi
        }
    }

    @PostMapping("/resend-verification-code")
    public Object resendVerificationCode(@Valid String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return "activation_account";
        } catch (MessagingException e) {
            return status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send verification code");
        }
    }

    @PostMapping("/change-password")
    public String changePassword(@RequestBody @Valid UserChangepasswordDTO request) {
            authenticationService.changePassword(request);
           return "user/home";

    }

    @PostMapping("/forgot-password")
    public ApiResponse<AuthenticationResponse> forgotPassword(@RequestParam @Valid String email) throws MessagingException {
        authenticationService.forgotPassword(email);
        return ApiResponse.<AuthenticationResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Password reset link sent successfully")
                .build();
    }

    @PreAuthorize("hasRole('ROLE_COMPANY')")
    @GetMapping("/company")
    public ApiResponse<String> com(){
        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Company")
                .build();
    }
}
