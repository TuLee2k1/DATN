package poly.com.controller.users;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import poly.com.dto.response.Auth.AuthenticationResponse;
import poly.com.service.ThongkeService;

import java.util.Map;

@Controller
@RequestMapping("users/dashboard")
public class UserDashboardController {

    @Autowired
    ThongkeService thongkeService;

    @GetMapping("")
    public String listAll(ModelMap model, HttpSession session) {


        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");
        System.out.println(user.getEmail());
        System.out.println(user.getId());

        Long userId = user.getId();

        // Lấy số lượng công việc đã ứng tuyển của user theo userId
        Long appliedJobsForUser = thongkeService.getAppliedJobsByUserId(userId);
        System.out.println(appliedJobsForUser);
        // Lấy số lượng bookmarks của user theo userId
        Long bookmarkedForUser = thongkeService.getBookmarkedByUserId(userId);
        System.out.println(bookmarkedForUser);



        // Lấy các số liệu thống kê khác từ service
        Map<String, Long> stats = thongkeService.getStatistics();

//        // Truyền các số liệu thống kê vào model
//        model.addAttribute("totalResumes", stats.get("totalResumes"));
        model.addAttribute("approvedResumes", stats.get("approvedResumes"));

        // Truyền số liệu công việc đã được user đánh dấu yêu thích (bookmarked) vào model
        model.addAttribute("bookmarkedForUser", bookmarkedForUser);
        // Truyền số liệu công việc đã ứng tuyển của user vào model
        model.addAttribute("applied", appliedJobsForUser);



        return "User/V3/dashboard";
    }
}