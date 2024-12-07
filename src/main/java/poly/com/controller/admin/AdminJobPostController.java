package poly.com.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.com.Enum.StatusEnum;
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

    @GetMapping("details/{id}")
    public String getJobPostDetails(@PathVariable("id") Long id, Model model) {
        JobPost jobPost = jobPostService.findById(id); // Sử dụng query với JOIN FETCH
        if (jobPost == null) {
            model.addAttribute("error", "JobPost not found!");
            return "error"; // Hoặc một trang báo lỗi phù hợp
        }
        model.addAttribute("jobPost", jobPost);
        return "/admin/jobpost-details/jobpost-detail";
    }

    @GetMapping
    public String list(@RequestParam(value = "statusEnum", required = false) String statusEnum, ModelMap model) {
        List<JobPost> jobPosts;

        if (statusEnum != null && !statusEnum.isEmpty()) {
            try {
                StatusEnum status = StatusEnum.fromString(statusEnum); // Convert string to enum
                jobPosts = jobPostService.findByStatusEnum(status);
            } catch (IllegalArgumentException e) {
                model.addAttribute("error", "Invalid status value!"); // Báo lỗi nếu trạng thái không hợp lệ
                jobPosts = jobPostService.findAll(); // Lấy tất cả bài đăng nếu lỗi
            }
        } else {
            jobPosts = jobPostService.findAll(); // Lấy tất cả bài đăng nếu không có trạng thái lọc
        }

        model.addAttribute("jobPosts", jobPosts);
        model.addAttribute("statusEnum", statusEnum); // Lưu trạng thái hiện tại cho view
        return "admin/jobposts/jobpost";
    }
}
