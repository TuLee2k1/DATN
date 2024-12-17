package poly.com.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
import poly.com.Enum.Exp;
import poly.com.Enum.JobLevel;
import poly.com.Enum.StatusEnum;
import poly.com.Enum.WorkType;
import poly.com.dto.request.JobPost.JobPostRequest;
import poly.com.dto.response.JobPost.JobListActiveResponse;
import poly.com.model.JobPost;

import poly.com.model.Profile;
import poly.com.repository.CompanyRepository;
import poly.com.repository.JobPostRepository;
import poly.com.repository.ProfileRepository;
import poly.com.repository.UserRepository;

import poly.com.service.JobCategoryService;
import poly.com.service.JobPostService;
import poly.com.service.SubCategoryService;
import poly.com.service.ThongkeService;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Controller
@RequiredArgsConstructor
@RequestMapping("")
public class HomeController {
    private final JobPostService jobPostService;
    private final JobCategoryService jobCategoryService;
    private final SubCategoryService subCategoryService;

    private final JobPostRepository jobPostRepository;

    @Autowired
    ThongkeService thongkeService;


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


        //thongke
        Map<String, Long> stats = thongkeService.getStatistics();

        // Truyền các số liệu thống kê vào model
        model.addAttribute("totalUser", stats.get("totalUser"));
        model.addAttribute("totalCompanies", stats.get("totalCompanies"));
        model.addAttribute("totalJobPosts", stats.get("totalJobPosts"));
        model.addAttribute("totalJobProfiles", stats.get("totalJobProfiles"));


        JobPostRequest jobPostRequest =
                new JobPostRequest(); // Tạo đối tượng mới để tạo bài đăng
        System.out.println("JobPostController2.showJobPostForm:  " + jobPostRequest.getId());

        model.addAttribute("jobPostRequest", jobPostRequest);
        System.out.println("Show thong tin jobPostRequest nhận đuọc :  " + jobPostRequest);

        return "fragments/home";
    }

    @PostMapping("/Tim-kiem")
    public String getJobListings(
            @RequestParam(defaultValue = "ACTIVE") String status,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Long jobCategory,
            @RequestParam(required = false) Exp exp,
            @RequestParam(required = false) WorkType workType,
            @RequestParam(required = false) JobLevel jobLevel,
            @RequestParam(required = false) Integer page, // Tham số trang hiện tại
            @RequestParam(defaultValue = "10") Integer size, // Kích thước trang
            Model model) {

        // Nếu pageNo không được truyền từ frontend, gán giá trị mặc định là 1
        if (page == null) {
            page = 1;
        }

        System.out.println("JobType: " + jobType);

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
        List<JobListActiveResponse> filteredJobList = filterJobListings(jobListActiveResponses, searchTerm, jobType, location, jobCategory,exp,workType,jobLevel);

        System.out.println("Filtered: " + filteredJobList.size());

        // Tính toán chỉ số bắt đầu và kết thúc cho phân trang
        int totalElements = filteredJobList.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int start = (page - 1) * size;
        int end = Math.min(start + size, totalElements);

        // Lấy danh sách công việc cho trang hiện tại
        List<JobListActiveResponse> paginatedJobList = filteredJobList.subList(start, end);

        JobPostRequest jobPostRequest = new JobPostRequest(); // Tạo đối tượng mới để tạo bài đăng

        model.addAttribute("jobPostRequest", jobPostRequest);
        System.out.println("JobPost: " + jobPostRequest);

        model.addAttribute("jobCategories", jobCategoryService.getAllJobCategories());
        model.addAttribute("subCategories", subCategoryService.getAllSubCategories());

        // Cập nhật model
        model.addAttribute("jobListings", paginatedJobList);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("currentPage", page); // Trang hiện tại
        model.addAttribute("totalPages", totalPages); // Tổng số trang
        model.addAttribute("provinces", getAllProvinces());
        System.out.println(getAllProvinces().size());
        // Thêm các giá trị vào model để giữ lại trong view
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("selectedJobCategoryId", jobCategory); // Đảm bảo giá trị này được gửi
        model.addAttribute("location", location);
        model.addAttribute("Exp", exp);// Đảm bảo giá trị này được gửi

        return "user/Search"; // Tên của template Thymeleaf
    }



    @GetMapping("/Tim-kiem")
    public String getJobListingsPage(
            @RequestParam(defaultValue = "ACTIVE") String status,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Long jobCategory,
            @RequestParam(required = false) Exp exp,
            @RequestParam(required = false) WorkType workType,
            @RequestParam(required = false) JobLevel jobLevel,
            @RequestParam(required = false) Integer page, // Tham số trang hiện tại
            @RequestParam(defaultValue = "10") Integer size, // Kích thước trang
            @RequestParam(required = false) String sortBy, // Tham số sắp xếp
            Model model) {

        System.out.println("Provinces: " + getAllProvinces().size());

        // Nếu page không được truyền từ frontend, gán giá trị mặc định là 1
        if (page == null) {
            page = 1;
        }

        // Chuyển đổi từ String sang StatusEnum
        StatusEnum statusEnum;
        try {
            statusEnum = StatusEnum.fromString(status);
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", "Invalid status value: " + status);
            return "user/Search"; // Trả về trang tìm kiếm với thông báo lỗi
        }

        model.addAttribute("provinces", getAllProvinces());
        System.out.println(getAllProvinces().size());
        // Thêm các giá trị vào model để giữ lại trong view
        model.addAttribute("searchTerm", searchTerm);
        model.addAttribute("selectedJobCategoryId", jobCategory); // Đảm bảo giá trị này được gửi
        model.addAttribute("location", location);
        model.addAttribute("Exp", exp);// Đảm bảo giá trị này được gửi
        model.addAttribute("jobLevel", jobLevel);// Đảm bảo giá trị này được gửi
        model.addAttribute("WorkType", workType);

        // Lấy tất cả các công việc với trạng thái cụ thể
        List<JobPost> jobPosts = jobPostService.getJobPostsByStatus(statusEnum);

        // Chuyển đổi danh sách JobPost sang JobListActiveResponse
        List<JobListActiveResponse> jobListActiveResponses = convertToJobListActiveResponse(jobPosts);

        // Bước 2: Lọc theo searchTerm, jobType và location
        List<JobListActiveResponse> filteredJobList = filterJobListings(jobListActiveResponses, searchTerm, jobType, location, jobCategory, exp, workType, jobLevel);

        System.out.println("Trước khi sắp xếp:");
        filteredJobList.forEach(job -> System.out.println(job.getCreateDate()));
        // Bước 3: Sắp xếp danh sách công việc dựa trên sortBy
        if (sortBy != null) {
            switch (sortBy) {
                case "newest":
                    // Sắp xếp theo ngày đăng (newest)
                    filteredJobList.sort((job1, job2) -> {
                        if (job1.getCreateDate() == null && job2.getCreateDate() == null) return 0;
                        if (job1.getCreateDate() == null) return 1; // Đưa job1 xuống cuối
                        if (job2.getCreateDate() == null) return -1; // Đưa job2 xuống cuối
                        return job1.getCreateDate().compareTo(job2.getCreateDate());
                    });
                    System.out.println("Sau khi sắp xếp:");
                    filteredJobList.forEach(job -> System.out.println(job.getCreateDate()));
                    break;
                case "highestSalary":
                    filteredJobList.sort((job1, job2) -> {
                        if (job1.getMaxSalary() == null && job2.getMaxSalary() == null) return 0;
                        if (job1.getMaxSalary() == null) return 1; // Đưa job1 xuống cuối
                        if (job2.getMaxSalary() == null) return -1; // Đưa job2 xuống cuối
                        return job1.getMaxSalary().compareTo(job2.getMaxSalary());
                    });
                    break;
                case "relevance":
                default:
                    // Giữ nguyên thứ tự hiện tại hoặc sắp xếp theo cách khác nếu cần
                    break;
            }
        }

        // Tính toán chỉ số bắt đầu và kết thúc cho phân trang
        int totalElements = filteredJobList.size();
        int totalPages = (int) Math.ceil((double) totalElements / size);
        int start = (page - 1) * size;
        int end = Math.min(start + size, totalElements);

        // Lấy danh sách công việc cho trang hiện tại
        List<JobListActiveResponse> paginatedJobList = filteredJobList.subList(start, end);

        JobPostRequest jobPostRequest = new JobPostRequest(); // Tạo đối tượng mới để tạo bài đăng

        model.addAttribute("jobPostRequest", jobPostRequest);
        model.addAttribute("jobCategories", jobCategoryService.getAllJobCategories());
        model.addAttribute("subCategories", subCategoryService.getAllSubCategories());

        // Cập nhật model
        model.addAttribute("jobListings", paginatedJobList);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("currentPage", page); // Trang hiện tại
        model.addAttribute("totalPages", totalPages); // Tổng số trang




        return "user/Search"; // Tên của template Thymeleaf
    }

    public List<String> getAllProvinces() {
        List<String> provinces = Arrays.asList(
                "Hà Nội", "Hồ Chí Minh", "Đà Nẵng", "Hải Phòng", "Cần Thơ",
                "An Giang", "Bà Rịa - Vũng Tàu", "Bắc Giang", "Bắc Kạn", "Bến Tre",
                "Bình Định", "Bình Dương", "Bình Phước", "Cà Mau", "Đắk Lắk",
                "Đắk Nông", "Điện Biên", "Đồng Nai", "Đồng Tháp", "Gia Lai",
                "Hà Giang", "Hà Nam", "Hà Tĩnh", "Hải Dương", "Hậu Giang",
                "Hòa Bình", "Hưng Yên", "Khánh Hòa", "Kiên Giang", "Kon Tum",
                "Lai Châu", "Lạng Sơn", "Lào Cai", "Long An", "Nam Định",
                "Nghệ An", "Ninh Bình", "Ninh Thuận", "Phú Thọ", "Phú Yên",
                "Quảng Bình", "Quảng Nam", "Quảng Ngãi", "Quảng Ninh", "Quảng Trị",
                "Sóc Trăng", "Sơn La", "Tây Ninh", "Thái Bình", "Thái Nguyên",
                "Thanh Hóa", "Thừa Thiên - Huế", "Tiền Giang", "Trà Vinh", "Tuyên Quang",
                "Vĩnh Long", "Vĩnh Phúc", "Yên Bái"
        );
        System.out.println("Number of provinces: " + provinces.size()); // In ra số lượng tỉnh thành
        return provinces;
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
                    response.setJobCategoryId(jobPost.getJobCategory().getId());
                    response.setExp(jobPost.getExp());
                    response.setWorkType(jobPost.getWorkType());
                    response.setJobLevel(jobPost.getJobLevel());
                    response.setCreateDate(jobPost.getCreateDate());
                    response.setMaxSalary(jobPost.getMaxSalary());
                    // Thiết lập các thuộc tính khác nếu cần
                    return response;
                })
                .collect(Collectors.toList());
    }

    // Phương thức lọc danh sách JobListActiveResponse
    private List<JobListActiveResponse> filterJobListings(List<JobListActiveResponse> jobListActiveResponses,
                                                          String searchTerm,
                                                          String jobType,
                                                          String location,
                                                          Long jobCategory,
                                                          Exp exp,
                                                          WorkType workType,
                                                          JobLevel jobLevel) {
        if (searchTerm != null && !searchTerm.isEmpty()) {
            jobListActiveResponses = jobPostService.filterBySearchTerm(jobListActiveResponses, searchTerm);
        }
        if (jobType != null && !jobType.isEmpty()) {
            jobListActiveResponses = jobPostService.filterByJobType(jobListActiveResponses, jobType);
        }
        if (location != null && !location.isEmpty()) {
            jobListActiveResponses = jobPostService.filterByLocation(jobListActiveResponses, location);
        }
        if (jobCategory != null) {
            jobListActiveResponses = jobPostService.filterByJobCategory(jobListActiveResponses, jobCategory);
        }
        if (exp!= null) {
            jobListActiveResponses = jobPostService.filterByExp(jobListActiveResponses, exp);
        }
        if (workType!= null) {
            jobListActiveResponses = jobPostService.filterByWorkType(jobListActiveResponses, workType);
        }
        if (jobLevel!= null) {
            jobListActiveResponses = jobPostService.filterByJobLevel(jobListActiveResponses, jobLevel);
        }
        return jobListActiveResponses;
    }

    @GetMapping("/about")
    public String about(Model model) {
        return "fragments/about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        return "fragments/contact";
    }


}
