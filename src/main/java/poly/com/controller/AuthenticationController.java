package poly.com.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import poly.com.dto.request.Auth.AuthRegisterRequest;
import poly.com.dto.request.Auth.LoginRequest;
import poly.com.dto.request.Auth.RegisterCompanyRequest;
import poly.com.dto.request.Auth.UserChangepasswordDTO;
import poly.com.dto.response.Auth.AuthenticationResponse;
import poly.com.exception.ApiResponse;
import poly.com.service.AuthenticationService;

import java.io.IOException;

import static org.springframework.http.ResponseEntity.status;
@Tag(name = "Authentication Controller")
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

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
    public ApiResponse<AuthenticationResponse> createCompany(@Valid @RequestBody RegisterCompanyRequest request) throws IllegalAccessException,
    MessagingException {
        //authenticationService.registerCompany(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Company registered successfully")
         .Result(authenticationService.registerCompany(request))
                .build();
    }

    @GetMapping("login")
    @ResponseStatus(HttpStatus.OK)
    public String login (){
        return "user/login";
    }

    @PostMapping("/authenticate")
    public void authenticate(@RequestParam String email, @RequestParam String password, HttpServletResponse response) throws IOException {
        LoginRequest loginRequest = new LoginRequest(email, password); // Tạo đối tượng LoginRequest từ các tham số

        AuthenticationResponse authResponse = authenticationService.authenticate(loginRequest);

        // Kiểm tra xem xác thực có thành công không
        if (authResponse != null) {
            // Nếu xác thực thành công, chuyển hướng đến trang dashboard
            response.sendRedirect("/auth/dashboard"); // Thay đổi '/dashboard' thành URL của trang dashboard của bạn
        } else {
            // Nếu xác thực không thành công, bạn có thể trả về mã lỗi hoặc thông báo
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
        }
    }
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
