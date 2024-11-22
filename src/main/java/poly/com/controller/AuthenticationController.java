package poly.com.controller;

import com.microsoft.sqlserver.jdbc.StringUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import poly.com.service.AuthenticationService;

import java.io.IOException;

import static org.springframework.http.ResponseEntity.status;
@Tag(name = "Authentication Controller")
@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
@SessionAttributes("user")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);
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
    public String authenticate(
     @RequestParam String email,
     @RequestParam String password,
     Model model,
     HttpServletRequest request,
     RedirectAttributes redirectAttributes
    ) {
        try {
            LoginRequest loginRequest = new LoginRequest(email, password);
            AuthenticationResponse authResponse = authenticationService.authenticate(loginRequest, request);

            // Lưu thông tin chi tiết vào session
            HttpSession session = request.getSession(true);
            session.setAttribute("user", authResponse);
            session.setAttribute("userId", authResponse.getId());
            session.setAttribute("userEmail", authResponse.getEmail());
            session.setAttribute("userRoles", authResponse.getRoles());


            // Thêm thông tin vào redirectAttributes
            redirectAttributes.addFlashAttribute("user", authResponse);

            return determineRedirectPage(authResponse);

        } catch (AuthenticationFailedException e) {
            model.addAttribute("error", e.getMessage());
            return "user/login";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        redirectAttributes.addFlashAttribute("message", "Đăng xuất thành công");
        return "redirect:/auth/login";
    }

    private String determineRedirectPage(AuthenticationResponse authResponse) {
        // Logic chuyển hướng dựa trên vai trò
        if (authResponse.getRoles().contains(RoleType.ROLE_COMPANY)) {
            System.out.println("Company =============================");
            return "redirect:/job-posts";
        } else if (authResponse.getRoles().contains(RoleType.ROLE_EMPLOYEE)) {
            return "redirect:/dashboard";
        } else {
            return "redirect:/dashboard";
        }
    }


    @GetMapping("/dashboard")
    @ResponseStatus(HttpStatus.OK)
    public String dashboard (Model model) {
        return

                "user/dashboard";
    }

    @PostMapping("/refresh-token")
    public void  refreshToken(HttpServletRequest request,
                              HttpServletResponse response) throws IOException {
        authenticationService.refreshToken(request, response);
    }
    private static final Logger logger1 = LoggerFactory.getLogger(AuthenticationController.class);

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

//    @PreAuthorize("hasRole('ROLE_COMPANY')")
//    @GetMapping("/company")
//    public ApiResponse<String> com(){
//        return ApiResponse.<String>builder()
//                .status(HttpStatus.OK.value())
//                .message("Company")
//                .build();
    //}

//    @PostMapping("/authenticate")
//    public String authenticate(
//     @RequestParam String email,
//     @RequestParam String password,
//     Model model,
//     RedirectAttributes redirectAttributes,
//     HttpSession session
//    ) {
//        try {
//            // Validate email và password
//            if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)) {
//                redirectAttributes.addFlashAttribute("error", "Email và mật khẩu không được để trống");
//                return "redirect:/login";
//            }
//
//            // Tạo đối tượng LoginRequest với email và password người dùng nhập
//            LoginRequest loginRequest = new LoginRequest(email, password);
//
//            // Gọi service xác thực đăng nhập
//            var authResponse = authenticationUtil.getAuthenticatedUser()
//
//            if (authResponse != null) {
//                // Lưu thông tin người dùng vào session
//                session.setAttribute("currentUser", authResponse);
//
//                // Phân quyền điều hướng dựa trên vai trò
//                switch (authResponse.getRoles()) {
//                    case COMPANY:
//                        return "redirect:/company/dashboard";
//                    case CANDIDATE:
//                        return "redirect:/candidate/profile";
//                    case ADMIN:
//                        return "redirect:/admin/dashboard";
//                    default:
//                        redirectAttributes.addFlashAttribute("error", "Vai trò không hợp lệ");
//                        return "redirect:/login";
//                }
//            } else {
//                // Xác thực thất bại
//                redirectAttributes.addFlashAttribute("error", "Email hoặc mật khẩu không đúng");
//                return "redirect:/login";
//            }
//        } catch (BadCredentialsException e) {
//            // Xử lý ngoại lệ xác thực
//            redirectAttributes.addFlashAttribute("error", "Đăng nhập thất bại: " + e.getMessage());
//            return "redirect:/login";
//        } catch (Exception e) {
//            // Ghi log lỗi
//            log.error("Lỗi đăng nhập: ", e);
//            redirectAttributes.addFlashAttribute("error", "Có lỗi xảy ra. Vui lòng thử lại.");
//            return "redirect:/login";
//        }
//    }
}
