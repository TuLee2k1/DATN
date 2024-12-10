package poly.com.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.com.Enum.StatusEnum;
import poly.com.model.JobPost;
import poly.com.service.JobPostService;
import poly.com.service.MapValidationErrorService;

@Controller
@RequestMapping("admin/jobposts")
public class AdminJobPostController {

    @Autowired
    JobPostService jobPostService;

    @Autowired
    MapValidationErrorService mapValidationErrorService;

//    @RequestMapping
//    public String list(ModelMap model) {
//        List<JobPost> list = jobPostService.findAll(); // Lấy tất cả Job Posts
//        model.addAttribute("jobPosts", list); // Truyền danh sách Job Posts sang view
//        return "admin/jobposts/jobpost"; // Trỏ đến file Thymeleaf hiển thị danh sách
//    }



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
    public String list(
            @RequestParam(value = "statusEnum", required = false) String statusEnum,
            @RequestParam(defaultValue = "1") Integer pageNo,
            ModelMap model) {

        Page<JobPost> jobPosts;

        if (statusEnum != null && !statusEnum.isEmpty()) {
            try {
                // Convert string to StatusEnum
                StatusEnum status = StatusEnum.fromString(statusEnum);
                // Gọi service để lấy danh sách theo trạng thái
                jobPosts = jobPostService.getJobListingsAdmin(pageNo, statusEnum);
            } catch (IllegalArgumentException e) {
                // Báo lỗi nếu giá trị trạng thái không hợp lệ
                model.addAttribute("error", "Invalid status value!");
                // Lấy tất cả bài đăng không lọc
                jobPosts = jobPostService.getJobListingsAdmin(pageNo, null);
            }
        } else {
            // Lấy tất cả bài đăng không lọc
            jobPosts = jobPostService.getJobListingsAdmin(pageNo, null);
        }

        // Thêm dữ liệu vào ModelMap
        model.addAttribute("jobPostsAd", jobPosts);
        model.addAttribute("statusEnum", statusEnum); // Lưu trạng thái hiện tại cho view

        return "admin/jobposts/jobpost"; // Trả về tên template
    }
}
