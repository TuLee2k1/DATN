package poly.com.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import poly.com.model.User;
import poly.com.service.AuthenticationService;

import java.util.List;

@Controller
@RequestMapping("admin/users")
public class AdminUserController {

    @Autowired
    AuthenticationService authenticationService;

    @GetMapping
    public String getAllUsers(Model model) {
        List<User> users = authenticationService.findAllUsers();
        model.addAttribute("users", users); // Đẩy danh sách user vào model
        return "admin/users/user"; // Tên của file HTML trong thư mục templates
    }
}
