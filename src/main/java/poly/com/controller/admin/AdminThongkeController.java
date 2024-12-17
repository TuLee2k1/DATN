package poly.com.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import poly.com.model.JobCategory;
import poly.com.service.ThongkeService;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("admin/thongke")
public class AdminThongkeController {

    @Autowired
    ThongkeService thongkeService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public String listAll(ModelMap model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Long> stats = thongkeService.getStatistics();

        // Truyền các số liệu thống kê vào model
        model.addAttribute("totalUser", stats.get("totalUser"));
        model.addAttribute("totalCompanies", stats.get("totalCompanies"));
        model.addAttribute("totalJobPosts", stats.get("totalJobPosts"));
        model.addAttribute("totalJobProfiles", stats.get("totalJobProfiles"));

        return "admin/thongke/thongke";  // Trả về view thống kê
    }
}
