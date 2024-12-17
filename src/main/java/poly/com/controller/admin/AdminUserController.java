package poly.com.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public String getAllUsers(@RequestParam(defaultValue = "1") Integer pageNo,
                              @RequestParam(required = false) String email,
                              Model model) {
        // Lấy danh sách người dùng với phân trang và tìm kiếm theo email
        Page<User> users = userService.users(email, pageNo);

        // Đưa dữ liệu vào model
        model.addAttribute("users", users); // Đẩy danh sách user vào model
        model.addAttribute("currentPage", pageNo); // Trang hiện tại
        model.addAttribute("totalPages", users.getTotalPages()); // Tổng số trang
        model.addAttribute("searchKeyword", email); // Từ khóa tìm kiếm (email)

        return "admin/users/user"; // Tên của file HTML trong thư mục templates
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{id}/lock")
    public String lockUserAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean success = userService.lockAccount(id);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Khóa tài khoản thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
        }
        return "redirect:/admin/users"; // Quay lại trang danh sách người dùng
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/{id}/unlock")
    public String unlockUserAccount(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean success = userService.unlockAccount(id);
        if (success) {
            redirectAttributes.addFlashAttribute("message", "Mở khóa tài khoản thành công!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy người dùng!");
        }
        return "redirect:/admin/users"; // Quay lại trang danh sách người dùng
    }


}
