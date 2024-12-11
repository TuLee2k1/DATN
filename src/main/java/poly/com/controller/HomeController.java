package poly.com.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import poly.com.Enum.StatusEnum;
import poly.com.dto.response.JobPost.JobListActiveResponse;
import poly.com.model.JobPost;
import poly.com.repository.JobPostRepository;
import poly.com.service.JobPostService;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@RequestMapping("")
public class HomeController {
    private final JobPostService jobPostService;
    private final JobPostRepository jobPostRepository;


    @GetMapping("")
    public String getJobListingsByStatus(
            @RequestParam(defaultValue = "ACTIVE") String status,
            @RequestParam(required = false) Integer pageNo, // Không có giá trị mặc định
            @RequestParam(defaultValue = "5") Integer pageSize, // Đặt pageSize mặc định là 5
            Model model) {

        // Nếu pageNo không được truyền từ frontend, gán giá trị mặc định là 1
        if (pageNo == null) {
            pageNo = 1;
        }

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

    @PostMapping("/Tim-kiem")
    public String getJobListings(
            @RequestParam(defaultValue = "ACTIVE") String status,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String location,
            Model model) {

        // Chuyển đổi từ String sang StatusEnum
        StatusEnum statusEnum;
        try {
            statusEnum = StatusEnum.fromString(status);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Invalid status value: " + status);
            return "user/Search"; // Trả về trang tìm kiếm với thông báo lỗi
        }

        // Lấy tất cả các công việc với trạng thái cụ thể
        List<JobPost> jobPosts = jobPostService.getJobPostsByStatus(statusEnum);

        // Chuyển đổi danh sách JobPost sang JobListActiveResponse
        List<JobListActiveResponse> jobListActiveResponses = convertToJobListActiveResponse(jobPosts);

        // Bước 2: Lọc theo searchTerm, jobType và location
        List<JobListActiveResponse> filteredJobList = filterJobListings(jobListActiveResponses, searchTerm, jobType, location);

        System.out.println("Filer: "+filteredJobList.size());

        // Cập nhật model
        model.addAttribute("jobListings", filteredJobList);
        model.addAttribute("totalElements", filteredJobList.size());
        model.addAttribute("currentPage", 1); // Không có phân trang, nên trang hiện tại là 1
        model.addAttribute("totalPages", 1); // Chỉ có một trang

        return "user/Search"; // Tên của template Thymeleaf
    }

    // Phương thức chuyển đổi từ JobPost sang JobListActiveResponse
    private List<JobListActiveResponse> convertToJobListActiveResponse(List<JobPost> jobPosts) {
        return jobPosts.stream()
                .map(jobPost -> {
                    JobListActiveResponse response = new JobListActiveResponse();
                    response.setId(jobPost.getId());
                    response.setJobTitle(jobPost.getJobTitle());
                    response.setCompanyName(jobPost.getCompany().getName());
                    response.setCity(jobPost.getCity());
                    response.setCompanyLogoUrl(jobPost.getCompany().getLogo());
                    response.setWorkType(jobPost.getWorkType());
                    // Thiết lập các thuộc tính khác nếu cần
                    return response;
                })
                .collect(Collectors.toList());
    }

    // Phương thức lọc danh sách JobListActiveResponse
    private List<JobListActiveResponse> filterJobListings(List<JobListActiveResponse> jobListActiveResponses,
                                                          String searchTerm,
                                                          String jobType,
                                                          String location) {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            jobListActiveResponses = jobPostService.filterBySearchTerm(jobListActiveResponses, searchTerm);
        }
        if (jobType != null && !jobType.isEmpty()) {
            jobListActiveResponses = jobPostService.filterByJobType(jobListActiveResponses, jobType);
        }
        if (location != null && !location.isEmpty()) {
            jobListActiveResponses = jobPostService.filterByLocation(jobListActiveResponses, location);
        }
        return jobListActiveResponses;
    }
}
