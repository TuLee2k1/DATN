package poly.com.controller.homeController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.com.dto.request.ApplyCVRequest;
import poly.com.model.JobPost;
import poly.com.model.JobProfile;
import poly.com.repository.JobPostRepository;
import poly.com.service.ApplyCVService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/job-application")
@RequiredArgsConstructor
public class JobApplicationController {

    private final JobPostRepository jobPostRepository;
    private final ApplyCVService applyCVService;
    private static String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/fileCV";
    // Hiển thị form ứng tuyển
    @GetMapping("/{jobPostId}")
    public String showApplicationForm(
     @PathVariable Long jobPostId,
     Model model
    ) {
        // Lấy thông tin job post
        JobPost jobPost = jobPostRepository.findById(jobPostId)
         .orElseThrow(() -> new RuntimeException("Job post not found"));

        // Tạo DTO để bind dữ liệu
        ApplyCVRequest applicationForm = new ApplyCVRequest();

        model.addAttribute("jobPost", jobPost);
        model.addAttribute("applicationForm", applicationForm);

        return "user/ApplyCV";
    }

    // Xử lý submit form
    @PostMapping("/{jobPostId}")
    public String submitApplication(
     @PathVariable Long jobPostId,
     @Valid @ModelAttribute("applicationForm") ApplyCVRequest applicationForm,
     BindingResult bindingResult,
     Model model,
     RedirectAttributes redirectAttributes
    ) throws IOException {
        // Kiểm tra validation
        if (bindingResult.hasErrors()) {
            // Nếu có lỗi, quay lại form
            return "user/ApplyCV";
        }

        String fileName = applicationForm.getResume().getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        Files.write(filePath, applicationForm.getResume().getBytes());


        // Kiểm tra file
        MultipartFile resume = applicationForm.getResume();
        if (resume == null || resume.isEmpty()) {
            bindingResult.rejectValue("resume", "error.resume", "Vui lòng chọn file CV");
            return "user/ApplyCV";
        }

        // Kiểm tra kích thước file (5MB)
        if (resume.getSize() > 5 * 1024 * 1024) {
            bindingResult.rejectValue("resume", "error.resume", "Kích thước file không được vượt quá 5MB");
            return "user/ApplyCV";
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
            return "user/ApplyCV";
        }

        try {
            // Gọi service để lưu CV
            JobProfile savedProfile = applyCVService.submitCv(resume, applicationForm, jobPostId);

            // Thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Ứng tuyển thành công!");

            return "redirect:/job-application/success";
        } catch (Exception e) {
            // Xử lý lỗi
            model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
            return "user/ApplyCV";
        }
    }

    // Trang thành công
    @GetMapping("/success")
    public String successPage() {
        return "user/home";
    }
}