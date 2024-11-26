package poly.com.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
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

    @RequestMapping
    public String listAll(ModelMap model) {
        Map<String, Long> stats = thongkeService.getStatistics();

        // Truyền các số liệu thống kê vào model
        model.addAttribute("totalCompanies", stats.get("totalCompanies"));
        model.addAttribute("totalJobPosts", stats.get("totalJobPosts"));
        model.addAttribute("totalJobProfiles", stats.get("totalJobProfiles"));

        return "admin/thongke/thongke";  // Trả về view thống kê
    }
}
