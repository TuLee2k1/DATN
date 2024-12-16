package poly.com.controller.homeController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.com.dto.request.ApplyCVRequest;
import poly.com.dto.request.JobPost.JobPostRequest;
import poly.com.model.JobPost;
import poly.com.model.JobProfile;
import poly.com.repository.JobPostRepository;
import poly.com.service.ApplyCVService;
import poly.com.service.JobPostService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/job-application")
@RequiredArgsConstructor
public class JobApplicationController {
    private final JobPostService jobPostService;
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

        return "fragments/job-single";
    }



    // Trang thành công
    @GetMapping("/success")
    public String successPage() {
        return "fragments/home";
    }
}