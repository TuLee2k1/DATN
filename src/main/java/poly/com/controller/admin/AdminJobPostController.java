package poly.com.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.com.dto.response.JobPost.JobPostResponse;
import poly.com.model.JobCategory;
import poly.com.model.JobPost;
import poly.com.service.JobPostService;
import poly.com.service.MapValidationErrorService;

import java.util.List;

@Controller
@RequestMapping("admin/jobposts")
public class AdminJobPostController {

    @Autowired
    JobPostService jobPostService;

    @Autowired
    MapValidationErrorService mapValidationErrorService;

    @RequestMapping
    public String list(ModelMap model) {
        List<JobPost> list = jobPostService.findAll(); // Lấy tất cả Job Posts
        model.addAttribute("jobPosts", list); // Truyền danh sách Job Posts sang view
        return "admin/jobposts/jobpost"; // Trỏ đến file Thymeleaf hiển thị danh sách
    }



    @GetMapping("delete/{id}")
    public ModelAndView delete(ModelMap model, @PathVariable("id") Long id ) {

        jobPostService.deleteById(id);

        model.addAttribute("message", "Job Post is deleted!");


        return new ModelAndView("forward:/admin/jobposts", model);
    }

    // Phương thức duyệt JobPost
    @GetMapping("/approve/{id}")
    public String approveJobPost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            jobPostService.approveJobPost(id); // Gọi service để duyệt
            redirectAttributes.addFlashAttribute("message", "Job post approved successfully.");
            return "redirect:/admin/jobposts"; // Điều hướng lại trang danh sách JobPosts
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("message", "Error: " + e.getMessage());
            return "redirect:/admin/jobposts"; // Quay lại danh sách JobPosts nếu có lỗi
        }
    }
}
