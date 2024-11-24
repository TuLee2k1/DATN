package poly.com.controller.CompanyController2;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.com.Enum.RoleType;
import poly.com.Enum.StatusEnum;
import poly.com.dto.request.JobPost.JobPostRequest;
import poly.com.dto.response.Auth.AuthenticationResponse;
import poly.com.dto.response.JobPost.JobListingResponse;
import poly.com.dto.response.JobPost.JobPostResponse;
import poly.com.dto.response.PageResponse;
import poly.com.service.JobCategoryService;
import poly.com.service.JobPostService;
import poly.com.service.SubCategoryService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/job-posts")
@Slf4j
@SessionAttributes("user")
public class JobPostController2 {

    private final JobPostService jobPostService;
    private final JobCategoryService jobCategoryService;
    private final SubCategoryService subCategoryService;

    @PreAuthorize("hasRole('ROLE_COMPANY')")
    @GetMapping()
    public String getJobPostPage(HttpSession session, Model model) {
        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");
        System.out.println("Authen get JobPostController2: " + user);

        // Kiểm tra quyền truy cập
        if (user == null || !user.getRoles().contains(RoleType.ROLE_COMPANY)) {
            return "redirect:/auth/login";
        }

        model.addAttribute("user", user);
        model.addAttribute("jobPostRequest", new JobPostRequest());
        model.addAttribute("jobCategories", jobCategoryService.getAllJobCategories());
        model.addAttribute("subCategories", subCategoryService.getAllSubCategories());
        return "Company/Taotintuyendung";
    }

    @PreAuthorize("hasRole('ROLE_COMPANY')")
    @PostMapping("/create")
    public String createJobPost(
            @Valid @ModelAttribute("jobPostRequest") JobPostRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model,
            HttpSession session
    ) {
        try {
            // Lấy user từ session
            AuthenticationResponse authUser = (AuthenticationResponse) session.getAttribute("user");

            if (authUser == null) {
                return "redirect:/auth/login";
            }

            // Kiểm tra role
            if (!authUser.getRoles().contains(RoleType.ROLE_COMPANY)) {
                redirectAttributes.addFlashAttribute("error", "Không có quyền truy cập");
                return "redirect:/auth/login";
            }

            // Xử lý validation errors
            if (bindingResult.hasErrors()) {
                prepareModelForForm(model, authUser);
                return "Company/Danhsachtindang";
            }

            // Set user ID vào request
            request.setUserId(authUser.getId());
            request.setUserEmail(authUser.getEmail());

            // Tạo job post
            JobPostResponse response = jobPostService.createJobPost(request);

            redirectAttributes.addFlashAttribute("successMessage", "Tạo bài đăng việc làm thành công!");
            System.out.println("Tạo bài đăng việc làm thành công!");
            return "redirect:/job-posts";

        } catch (Exception e) {
            log.error("Lỗi khi tạo job post: ", e);
            AuthenticationResponse authUser = (AuthenticationResponse) session.getAttribute("user");
            prepareModelForForm(model, authUser);
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "Company/Taotintuyendung";
        }
    }

    private void prepareModelForForm(Model model, AuthenticationResponse user) {
        model.addAttribute("user", user);
        model.addAttribute("jobCategories", jobCategoryService.getAllJobCategories());
        model.addAttribute("subCategories", subCategoryService.getAllSubCategories());
    }




    @GetMapping("/detail/{id}")
    public String showJobPostDetail(@PathVariable Long id, Model model) {
        JobPostResponse jobPost = jobPostService.getJobPost(id);
        model.addAttribute("jobPost", jobPost);
        return "job-post/detail";
    }

    @PreAuthorize("hasRole('ROLE_COMPANY')")
    @GetMapping("/Listing")
    public String getJobPostPage(
            @RequestParam(defaultValue = "Công nghệ thông tin") String jobTitle,
            @RequestParam(defaultValue = "PENDING") StatusEnum statusEnum,
            @RequestParam(defaultValue = "1") Integer pageNo,
            HttpSession session,
            Model model
    ) {
        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");

        if (user == null || !user.getRoles().contains(RoleType.ROLE_COMPANY)) {
            return "redirect:/auth/login";
        }

        // Add data for filters
        model.addAttribute("jobTitles", jobPostService.getJobPostTitle());
        model.addAttribute("statusEnums", StatusEnum.values());

        // Xử lý jobTitle empty
        if (jobTitle != null && jobTitle.trim().isEmpty()) {
            jobTitle = null;
        }
        System.out.println(jobTitle);
        System.out.println(statusEnum);
        System.out.println(pageNo);
        // Add pagination data
        PageResponse<JobListingResponse> jobListings = jobPostService.getJobListings(jobTitle, statusEnum, pageNo);
        model.addAttribute("jobListings", jobListings.getContent());
        System.out.println(jobListings.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", jobListings.getTotalPages());

        // Add selected filter values
        model.addAttribute("selectedJobTitle", jobTitle);
        model.addAttribute("selectedStatus", statusEnum);

        model.addAttribute("user", user);
        return "Company/Danhsachtindang";
    }

    @PreAuthorize("hasRole('ROLE_COMPANY')")
    @PostMapping("/{id}/toggle-view")
    @ResponseBody
    public ResponseEntity<?> toggleJobPostView(@PathVariable Long id, @RequestParam boolean enable) {
        try {
            if (enable) {
                jobPostService.enabledView(id);
            } else {
                jobPostService.disableView(id);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

//    @PreAuthorize("hasRole('ROLE_COMPANY')")
//    @GetMapping("/List")
//    public String getJobPostList(
//            HttpSession session,
//            Model model
//    ) {
//        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");
//
//        if (user == null || !user.getRoles().contains(RoleType.ROLE_COMPANY)) {
//            return "redirect:/auth/login";
//        }
//
//        // Add data for filters
//        model.addAttribute("jobTitles", jobPostService.getJobPostTitle());
//        model.addAttribute("statusEnums", StatusEnum.values());
//
//
//
//
//        // Add pagination data
//        PageResponse<JobListingResponse> jobList = jobPostService.getJobList(1);
//        model.addAttribute("jobListings", jobList.getContent());
//        model.addAttribute("totalPages", jobList.getTotalPages());
//
//
//
//        model.addAttribute("user", user);
//        return "Company/Danhsachtindang";
//    }
}