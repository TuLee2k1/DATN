package poly.com.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import poly.com.dto.response.JobPost.JobListActiveResponse;
import poly.com.dto.response.JobPost.JobListingResponse;
import poly.com.model.JobPost;
import poly.com.service.JobPostService;

@Controller
@RequiredArgsConstructor
@RequestMapping("")
public class HomeController {
    private final JobPostService jobPostService;

    @GetMapping("")
    public String getJobListingsByStatus(
            @RequestParam(defaultValue = "ACTIVE") String status,
            @RequestParam(defaultValue = "1") Integer pageNo,
            @RequestParam(defaultValue = "10") Integer pageSize,
            Model model) {

        Page<JobListActiveResponse> jobListings = jobPostService.getJobListingsByStatus(status, pageNo, pageSize);
    System.out.println(jobListings);
        for (JobListActiveResponse job : jobListings) {
            System.out.println("Job Title: " + job.getJobTitle());
            System.out.println("Company Name: " + job.getId());
            System.out.println("Processing Time: " + job.getCompanyId()); // Thay đổi theo tên phương thức thực tế
        }
        // Thêm dữ liệu vào model
        model.addAttribute("jobListings", jobListings);
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", jobListings.getTotalPages());
        model.addAttribute("status", status);

        return "fragments/home";
    }
}
