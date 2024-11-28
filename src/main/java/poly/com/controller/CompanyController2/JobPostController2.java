package poly.com.controller.CompanyController2;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.com.Enum.RoleType;
import poly.com.Enum.StatusEnum;
import poly.com.dto.request.ApplyCVRequest;
import poly.com.dto.request.JobPost.JobPostRequest;
import poly.com.dto.response.Auth.AuthenticationResponse;
import poly.com.dto.response.JobPost.JobListActiveResponse;

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

    // Phương thức hỗ trợ để chuẩn bị model cho form
    private void prepareModelForForm(Model model, AuthenticationResponse user) {
        model.addAttribute("user", user);
        model.addAttribute("jobCategories", jobCategoryService.getAllJobCategories());
        model.addAttribute("subCategories", subCategoryService.getAllSubCategories());
//            model.addAttribute("isEdit", false); // Thêm thuộc tính để xác định chế độ
    }

    // Kiểm tra quyền truy cập
    private boolean checkCompanyAccess(AuthenticationResponse user, RedirectAttributes redirectAttributes) {
        if (user == null || !user.getRoles().contains(RoleType.ROLE_COMPANY)) {
            redirectAttributes.addFlashAttribute("error", "Không có quyền truy cập");
            return false;
        }
        return true;
    }


    @PreAuthorize("hasRole('ROLE_COMPANY')")
    @GetMapping({"/create", "/update/{id}"})
    public String showJobPostForm(
            @PathVariable(required = false) Long id,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");

        if (!checkCompanyAccess(user, redirectAttributes)) {
            return "redirect:/auth/login";
        }

        JobPostRequest jobPostRequest = (id != null)
                ? jobPostService.getJobPost(id) // Lấy dữ liệu bài đăng để chỉnh sửa
                : new JobPostRequest(); // Tạo đối tượng mới để tạo bài đăng
        System.out.println("JobPostController2.showJobPostForm:  " + jobPostRequest.getId());

        model.addAttribute("jobPostRequest", jobPostRequest);
        System.out.println("Show thong tin jobPostRequest nhận đuọc :  " + jobPostRequest);
//            model.addAttribute("isEdit", jobPostRequest.getId()); // Chế độ cập nhật nếu có id
        prepareModelForForm(model, user);

        return "Company/Taotintuyendung";
    }



    @PreAuthorize("hasRole('ROLE_COMPANY')")
    @PostMapping({"/create", "/{id}/update"})
    public String saveJobPost(
            @PathVariable(required = false) Long id,
            @Valid @ModelAttribute("jobPostRequest") JobPostRequest request,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model,
            HttpSession session
    ) {
        var ID = request.getId();
        System.out.println("Xem ID nhận biết Create hay Update ID:  " + ID);
        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");

        if (!checkCompanyAccess(user, redirectAttributes)) {
            return "redirect:/auth/login";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", id != null);
            prepareModelForForm(model, user);
            return "Company/Taotintuyendung";
        }
        System.out.println("Xem ID nhận biết Create hay Update:  " + request.getId());
        try {
            if (id == null) {
                // Xử lý tạo mới
                request.setUserId(user.getId());
                request.setUserEmail(user.getEmail());
                jobPostService.createJobPost(request);
                redirectAttributes.addFlashAttribute("successMessage", "Tạo bài đăng thành công!");
            } else {
                // Xử lý cập nhật
                request.setId(id);
                jobPostService.updateJobPost(id, request);
                redirectAttributes.addFlashAttribute("successMessage", "Cập nhật bài đăng thành công!");
            }
            return "redirect:/job-posts/Listing";
        } catch (Exception e) {
            log.error("Lỗi khi xử lý bài đăng: ", e);
            model.addAttribute("isEdit", id != null);
            prepareModelForForm(model, user);
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "Company/Taotintuyendung";
        }
    }

    // Chi tiết bài đăng
    @GetMapping("/detail/{id}")
    public String showJobPostDetail(@PathVariable Long id, Model model) {
        JobPostRequest jobPost = jobPostService.getJobPost(id);


        // Lấy danh sách công việc
        Page<JobListActiveResponse> jobListings = jobPostService.getJobListingsByStatus("ACTIVE", 1, 10);
        model.addAttribute("jobListings", jobListings);
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", jobListings.getTotalPages());

        // Tạo DTO để bind dữ liệu
        ApplyCVRequest applicationForm = new ApplyCVRequest();
        model.addAttribute("applicationForm", applicationForm);
        model.addAttribute("jobPost", jobPost);

        return "fragments/job-single";
    }

    // Danh sách bài đăng
    @PreAuthorize("hasRole('ROLE_COMPANY')")
    @GetMapping("/Listing")
    public String getJobPostListing(
            @RequestParam(defaultValue = "") String jobTitle,
            @RequestParam(defaultValue = "") StatusEnum statusEnum,
            @RequestParam(defaultValue = "1") Integer pageNo,
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");

        if (!checkCompanyAccess(user, redirectAttributes)) {
            return "redirect:/auth/login";
        }

        // Xử lý jobTitle rỗng
        jobTitle = (jobTitle != null && jobTitle.trim().isEmpty()) ? null : jobTitle;

        // Thêm dữ liệu cho bộ lọc
        model.addAttribute("jobTitles", jobPostService.getJobPostTitle());
        model.addAttribute("statusEnums", StatusEnum.values());

        // Lấy danh sách bài đăng
        PageResponse<JobListingResponse> jobListings = jobPostService.getJobListings(jobTitle, statusEnum, pageNo);

        model.addAttribute("jobListings", jobListings.getContent());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", jobListings.getTotalPages());

        // Thêm giá trị bộ lọc đã chọn
        model.addAttribute("selectedJobTitle", jobTitle);
        model.addAttribute("selectedStatus", statusEnum);
        model.addAttribute("user", user);

        return "Company/Danhsachtindang";
    }

    // Bật/tắt chế độ hiển thị bài đăng
    @PreAuthorize("hasRole('ROLE_COMPANY')")
    @PostMapping("/{id}/toggle-view")
    @ResponseBody
    public ResponseEntity<?> toggleJobPostView(
            @PathVariable Long id,
            @RequestParam boolean enable
    ) {
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

}