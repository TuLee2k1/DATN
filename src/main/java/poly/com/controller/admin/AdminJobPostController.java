package poly.com.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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



    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("delete/{id}")
    public ModelAndView delete(ModelMap model, @PathVariable("id") Long id ) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        jobPostService.deleteById(id);

        model.addAttribute("message", "Job Post is deleted!");


        return new ModelAndView("forward:/admin/jobposts", model);
    }

    // Phương thức duyệt JobPost
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/approve/{id}")
    public String approveJobPost(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            jobPostService.approveJobPost(id); // Gọi service để duyệt
            redirectAttributes.addFlashAttribute("message", "Duyệt bài đăng thành công!");
            return "redirect:/admin/jobposts"; // Điều hướng lại trang danh sách JobPosts
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Bài đăng này đã bị xóa!");
            return "redirect:/admin/jobposts"; // Quay lại danh sách JobPosts nếu có lỗi
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("details/{id}")
    public String getJobPostDetails(@PathVariable("id") Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        JobPost jobPost = jobPostService.findById(id); // Sử dụng query với JOIN FETCH
        if (jobPost == null) {
            model.addAttribute("error", "JobPost not found!");
            return "error"; // Hoặc một trang báo lỗi phù hợp
        }
        model.addAttribute("jobPost", jobPost);
        return "/admin/jobpost-details/jobpost-detail";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public String list(
            @RequestParam(defaultValue = "statusEnum", required = false) String statusEnum,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(required = false) String jobTitle,  // Thêm tham số jobTitle
            ModelMap model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Page<JobPost> jobPosts;

        if (jobTitle != null && !jobTitle.isEmpty()) {
            // Nếu có tìm kiếm theo tiêu đề
            jobPosts = jobPostService.getJobListingsAdmin(pageNo, statusEnum, jobTitle);
        } else {
            if (statusEnum != null && !statusEnum.isEmpty()) {
                try {
                    jobPosts = jobPostService.getJobListingsAdmin(pageNo, statusEnum,jobTitle); // Lấy bài đăng theo trạng thái
                } catch (IllegalArgumentException e) {
                    model.addAttribute("error", "Invalid status value!");
                    jobPosts = jobPostService.getJobListingsAdmin(pageNo); // Lấy tất cả nếu trạng thái không hợp lệ
                }
            } else {
                jobPosts = jobPostService.getJobListingsAdmin(pageNo); // Lấy tất cả bài đăng nếu không có trạng thái
            }
        }

        // Thêm dữ liệu vào ModelMap
        model.addAttribute("jobPostsAd", jobPosts);
        model.addAttribute("statusEnum", statusEnum);
        model.addAttribute("jobListings", jobPosts.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", jobPosts.getTotalPages());
        model.addAttribute("selectedStatus", statusEnum);
        model.addAttribute("searchKeyword", jobTitle);  // Thêm vào model để trả về form tìm kiếm

        return "admin/jobposts/jobpost"; // Trả về template
    }



}
