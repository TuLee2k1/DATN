package poly.com.controller.admin;


import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.com.dto.response.Auth.AuthenticationResponse;
import poly.com.model.JobCategory;
import poly.com.model.User;
import poly.com.service.AuthenticationService;
import poly.com.service.MapValidationErrorService;
import poly.com.service.ProfileService;

import java.util.List;

@Controller
@RequestMapping("admin/profiles")
public class AdminProfileController {
    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    MapValidationErrorService mapValidationErrorService;

    @Autowired
    ProfileService profileService;

    @RequestMapping
    public String getAllProfiles(ModelMap model) {


        return "admin/profiles/profile";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/change-password-admin")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmNewPassword,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
        try {
            AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");

            if (user == null) {
                redirectAttributes.addFlashAttribute("error", "Người dùng chưa đăng nhập.");
                return "redirect:/auth/login";
            }

            // Kiểm tra mật khẩu mới có khớp không
            if (!newPassword.equals(confirmNewPassword)) {
                redirectAttributes.addFlashAttribute("error", "Mật khẩu mới không khớp.");
                return "redirect:/admin/profiles";
            }

            // Đổi mật khẩu
            profileService.changePassword(user.getId(), currentPassword, newPassword);

            redirectAttributes.addFlashAttribute("message", "Thay đổi mật khẩu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Thay đổi mật khẩu thất bại: " + e.getMessage());
        }
        return "redirect:/admin/profiles";
    }
}
