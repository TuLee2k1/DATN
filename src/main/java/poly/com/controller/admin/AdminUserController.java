package poly.com.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.com.model.User;
import poly.com.service.AuthenticationService;
import poly.com.service.UserService;

import java.util.List;

@Controller
@RequestMapping("admin/users")
public class AdminUserController {

    @Autowired
    AuthenticationService authenticationService;
    @Autowired
    private UserService userService;

    @GetMapping
    public String getAllUsers(Model model) {
        List<User> users = authenticationService.findAllUsers();
        model.addAttribute("users", users); // Đẩy danh sách user vào model
        return "admin/users/user"; // Tên của file HTML trong thư mục templates
    }

    @PostMapping("/{id}/lock")
    public String lockUserAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean success = userService.lockAccount(id);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Khóa tài khoản thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
        }
        return "redirect:/admin/users"; // Quay lại trang danh sách người dùng
    }

}
