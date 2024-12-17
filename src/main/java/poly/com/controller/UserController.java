package poly.com.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import poly.com.dto.response.Auth.AuthenticationResponse;
import poly.com.service.ProfileService;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private ProfileService profileService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/change-password")
    public String showChangePasswordPage(Model model, HttpSession session) {
        // Kiểm tra xem người dùng đã đăng nhập chưa
        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");
        if (user == null) {
            return "redirect:/auth/login";
        }
        return "User/V3/changepassword";
    }

    @PostMapping("/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            HttpSession session,
            Model model) {
        try {
            // Lấy thông tin người dùng từ session
            AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");

            if (user == null) {
                model.addAttribute("errorMessage", "Người dùng chưa đăng nhập.");
                return "redirect:/auth/login";
            }

            // Kiểm tra mật khẩu mới có khớp không
            if (!newPassword.equals(confirmPassword)) {
                model.addAttribute("errorMessage", "Mật khẩu mới không khớp.");
                return "User/V3/changepassword";
            }

            // Gọi service để đổi mật khẩu
            profileService.changePassword(user.getId(), currentPassword, newPassword);

            // Thêm thông báo thành công
            model.addAttribute("successMessage", "Thay đổi mật khẩu thành công!");
            return "User/V3/changepassword";

        } catch (IllegalArgumentException e) {
            // Bắt lỗi khi mật khẩu hiện tại không đúng
            model.addAttribute("errorMessage", e.getMessage());
            return "User/V3/changepassword";
        } catch (Exception e) {
            // Bắt các lỗi khác
            model.addAttribute("errorMessage", "Thay đổi mật khẩu thất bại: " + e.getMessage());
            return "User/V3/changepassword";
        }
    }
}
