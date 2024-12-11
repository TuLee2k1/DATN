package poly.com.controller.CompanyController2;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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
import poly.com.model.JobProfile;
import poly.com.repository.JobPostRepository;
import poly.com.service.ApplyCVService;
import poly.com.service.JobCategoryService;
import poly.com.service.JobPostService;
import poly.com.service.SubCategoryService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Controller
@RequiredArgsConstructor
@RequestMapping("/job-posts")
@Slf4j
@SessionAttributes("user")
public class JobPostController2 {

    private final JobPostService jobPostService;
    private final JobCategoryService jobCategoryService;
    private final SubCategoryService subCategoryService;
    private static String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/fileCV";
    private final JobPostRepository jobPostRepository;
    private final ApplyCVService applyCVService;

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

        Date DateNow = new Date();

        // Lấy danh sách công việc
        Page<JobListActiveResponse> jobListings = jobPostService.getJobListingsByStatus("ACTIVE", 1, 10);
        model.addAttribute("jobListings", jobListings);
        model.addAttribute("currentPage", 1);
        model.addAttribute("totalPages", jobListings.getTotalPages());
        model.addAttribute("currentDate", DateNow);

        // Tạo DTO để bind dữ liệu
        ApplyCVRequest applicationForm = new ApplyCVRequest();
        model.addAttribute("applicationForm", applicationForm);
        model.addAttribute("jobPost", jobPost);

        return "fragments/job-single";
    }

    // Xử lý submit form

    @PostMapping("/detail/{jobPostId}")
    public ResponseEntity<Map<String, String>> submitApplication(
            @PathVariable Long jobPostId,
            @Valid @ModelAttribute("applicationForm") ApplyCVRequest applicationForm,
            BindingResult bindingResult, Model model
    ) throws IOException {
        Map<String, String> response = new HashMap<>();

        JobPostRequest jobPost = jobPostService.getJobPost(jobPostId);
        model.addAttribute("jobPost", jobPost);
        // Kiểm tra validation
        if (bindingResult.hasErrors()) {
            // Nếu có lỗi, quay lại form
            System.out.println(bindingResult.getAllErrors());
            String errorMessage = bindingResult.getAllErrors().stream()
                    .map(Object::toString)
                    .collect(Collectors.joining(", "));
            response.put("errorMessage", "Vui lòng kiểm tra lại thông tin: " + errorMessage);
            return ResponseEntity.badRequest().body(response);
        }

        String fileName = applicationForm.getResume().getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        Files.write(filePath, applicationForm.getResume().getBytes());


        // Kiểm tra file
        MultipartFile resume = applicationForm.getResume();
        if (resume == null || resume.isEmpty()) {
            bindingResult.rejectValue("resume", "error.resume", "Vui lòng chọn file CV");
            response.put("errorMessage", "Vui lòng chọn file CV.");
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra kích thước file (5MB)
        if (resume.getSize() > 5 * 1024 * 1024) {
            bindingResult.rejectValue("resume", "error.resume", "Kích thước file không được vượt quá 5MB");
            response.put("errorMessage", "Kích thước file không được vượt quá 5MB.");
            return ResponseEntity.badRequest().body(response);
        }

        // Kiểm tra định dạng file
        String originalFilename = resume.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        String[] allowedExtensions = {"pdf", "doc", "docx"};

        boolean isValidExtension = false;
        for (String ext : allowedExtensions) {
            if (fileExtension.equals(ext)) {
                isValidExtension = true;
                break;
            }
        }

        if (!isValidExtension) {
            bindingResult.rejectValue("resume", "error.resume", "Chỉ chấp nhận file PDF, DOC, DOCX");
            response.put("errorMessage", "Chỉ chấp nhận file PDF, DOC, DOCX.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            System.out.println("Id Job: "+jobPostId);
            System.out.println("Thông tin: "+ applicationForm.getName());
            System.out.println("Thông tin resume: "+ resume);
            // Gọi service để lưu CV
            JobProfile savedProfile = applyCVService.submitCv(resume, applicationForm, jobPostId);
            System.out.println("thông tin save "+savedProfile);
            System.out.println("Id Job progile: "+savedProfile.getId());
            // Thông báo thành công
            response.put("successMessage","Ứng tuyển thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Xử lý lỗi
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            response.put("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
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

    // Xóa bài đăng
    @PreAuthorize("hasRole('ROLE_COMPANY')")
    @PostMapping("/{id}/delete")
    public String deleteJobPost(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            jobPostService.deleteJobPostByStatusEnum(id);
            redirectAttributes.addFlashAttribute("successMessage", "Xóa bài đăng thành công!");
        } catch (Exception e) {
            log.error("Lỗi khi xóa bài đăng: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
        }
        return "redirect:/job-posts/Listing";
    }

}