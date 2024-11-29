package poly.com.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import poly.com.Enum.StatusEnum;
import poly.com.dto.response.JobPost.JobListActiveResponse;
import poly.com.repository.CompanyRepository;
import poly.com.Util.AuthenticationUtil;

import poly.com.dto.request.JobPost.JobPostRequest;
import poly.com.dto.request.JobPost.JobPostTitleResponse;

import poly.com.dto.response.JobPost.JobListingResponse;
import poly.com.dto.response.JobPost.JobPostResponse;
import poly.com.dto.response.PageResponse;
import poly.com.exception.JobPostException;
import poly.com.model.*;

import poly.com.repository.JobCategoryRepository;
import poly.com.repository.JobPostRepository;
import poly.com.repository.SubCategoryRepository;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class JobPostService {
    private final JobPostRepository jobPostRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final AuthenticationUtil authenticationUtil;
    @Autowired
    private CompanyRepository companyRepository; // Repository để truy vấn thông tin công ty


    public void approveJobPost(Long jobPostId) {
        JobPost jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new RuntimeException("Job post not found"));
        if (jobPost.getStatusEnum() == StatusEnum.PENDING) {
            jobPost.setStatusEnum(StatusEnum.ACTIVE);
            jobPostRepository.save(jobPost);
        } else {
            throw new RuntimeException("Job post is not in PENDING status");
        }
    }

    public JobPostResponse createJobPost(JobPostRequest request) {
        User authenticatedUser = authenticationUtil.getAuthenticatedUser();
        Company company = authenticatedUser.getCompany();
        if (company == null) {
            throw new EntityNotFoundException("Người dùng chưa có thông tin công ty");
        }

        validateJobPostRequest(request);

        var jobCategory = findJobCategory(request.getJobCategoryId());
        var subCategory = findSubCategory(request.getSubCategoryIds());

        var jobPost = JobPost.builder()
                .jobTitle(request.getJobTitle())
                .jobDescription(request.getJobDescription())
                .quantity(request.getQuantity())
                .jobRequire(request.getJobRequire())
                .jobBenefit(request.getJobBenefit())
                .createDate(new Date())
                .endDate(request.getEndDate())
                .minSalary(request.getMinSalary())
                .maxSalary(request.getMaxSalary())
                .city(request.getCity())
                .district(request.getDistrict())
                .address(request.getAddress())
                .company(company)
                .jobCategory(jobCategory)
                .subCategory(subCategory)
                .workType(request.getWorkType())
                .jobLevel(request.getJobLevel())
                .exp(request.getExp())
                .status(JobPostStatus.Open)
                .statusEnum(StatusEnum.PENDING)
                .appliedCount(0)
                .build();

        var savedJobPost = jobPostRepository.save(jobPost);
        return JobPostResponse.fromEntity(savedJobPost);
    }

    public JobPostResponse updateJobPost(Long id, JobPostRequest request) {
        var jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy công việc"));

        validateJobPostRequest(request);

        var jobCategory = findJobCategory(request.getJobCategoryId());
        var subCategory = findSubCategory(request.getSubCategoryIds());

        // Cập nhật các trường
        jobPost.setJobTitle(request.getJobTitle());
        jobPost.setJobDescription(request.getJobDescription());
        jobPost.setQuantity(request.getQuantity());
        jobPost.setJobRequire(request.getJobRequire());
        jobPost.setJobBenefit(request.getJobBenefit());
        jobPost.setEndDate(request.getEndDate());
        jobPost.setMinSalary(request.getMinSalary());
        jobPost.setMaxSalary(request.getMaxSalary());
        jobPost.setCity(request.getCity());
        jobPost.setDistrict(request.getDistrict());
        jobPost.setAddress(request.getAddress());
        jobPost.setWorkType(request.getWorkType());
        jobPost.setJobLevel(request.getJobLevel());
        jobPost.setExp(request.getExp());
        jobPost.setJobCategory(jobCategory);
        jobPost.setSubCategory(subCategory);

        var savedJobPost = jobPostRepository.save(jobPost);
        return JobPostResponse.fromEntity(savedJobPost);
    }

    private void validateJobPostRequest(JobPostRequest request) {
        if (request.getEndDate().before(new Date())) {
            throw new IllegalArgumentException("Ngày kết thúc phải sau ngày tạo");
        }
        if (request.getMaxSalary() < request.getMinSalary()) {
            throw new IllegalArgumentException("Lương tối đa phải lớn hơn lương tối thiểu");
        }
    }

    private JobCategory findJobCategory(Long id) {
        return jobCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy ngành nghề"));
    }

    private SubCategory findSubCategory(Long id) {
        return subCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy chuyên ngành"));
    }


    public JobPostRequest getJobPost(Long id) {
        JobPost jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new JobPostException("Không tìm thấy công việc"));
        System.out.println("JobPostService.getJobPost:  " + jobPost.getId());

        return JobPostRequest.builder()
                .id(jobPost.getId()) // Thêm ID để phục vụ cho việc cập nhật
                .jobTitle(jobPost.getJobTitle())
                .jobDescription(jobPost.getJobDescription())
                .quantity(jobPost.getQuantity())
                .jobRequire(jobPost.getJobRequire())
                .jobBenefit(jobPost.getJobBenefit())
                .endDate(jobPost.getEndDate())
                .minSalary(jobPost.getMinSalary())
                .maxSalary(jobPost.getMaxSalary())
                .city(jobPost.getCity())
                .district(jobPost.getDistrict())
                .address(jobPost.getAddress())
                .workType(jobPost.getWorkType())
                .exp(jobPost.getExp())
                .jobLevel(jobPost.getJobLevel())
                .jobCategoryId(jobPost.getJobCategory().getId())
                .subCategoryIds(jobPost.getSubCategory().getId())
                .companyName(jobPost.getCompany().getName())
                .build();



    }


    public void deleteById(Long id) {
        JobPost jobPost = jobPostRepository.findById(id).orElseThrow(() -> new JobPostException("Không tìm thấy công " +
                "việc"));

        jobPost.setStatusEnum(StatusEnum.DELETED);
        jobPostRepository.save(jobPost);

    }


    public void disableView(Long id) {
        JobPost jobPost = jobPostRepository.findById(id).orElseThrow(() -> new JobPostException("Không tìm thấy công " +
                "việc"));

        jobPost.setStatus(JobPostStatus.Closed);
        jobPostRepository.save(jobPost);
    }

    public void enabledView(Long id) {
        JobPost jobPost = jobPostRepository.findById(id).orElseThrow(() -> new JobPostException("Không tìm thấy công " +
                "việc"));

        jobPost.setStatus(JobPostStatus.Open);
        jobPostRepository.save(jobPost);
    }

    public List<JobListingResponse> getJobListings() {
        var jobListings = jobPostRepository.findAll(); // Ensure this method returns List<JobPost>
        return jobListings.stream().map(this::convertToJobListingResponse).toList();
    }

    public Page<JobListingResponse> getJobListings(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        var jobListings = jobPostRepository.findAll(pageable);
        return jobListings.map(this::convertToJobListingResponse);
    }

    public PageResponse<JobListingResponse> getJobListings(String jobTitle, StatusEnum statusEnum, Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10);

        // Retrieve the authenticated user
        User authenticatedUser = authenticationUtil.getAuthenticatedUser();
        Company company = authenticatedUser.getCompany();
        if (company == null) {
            throw new EntityNotFoundException("Người dùng chưa có thông tin công ty");
        }

        // Filter job posts by company
        Page<JobListingResponse> jobPosts = jobPostRepository.findAllByJobTitleContainingAndStatusEnum(jobTitle, statusEnum,
                pageable, company);

        return new PageResponse<>(jobPosts);
    }

//    public PageResponse<JobListingResponse> getJobList( Integer pageNo) {
//        Pageable pageable = PageRequest.of(pageNo - 1, 10);
//
//        // Retrieve the authenticated user
//        User authenticatedUser = authenticationUtil.getAuthenticatedUser();
//        Company company = authenticatedUser.getCompany();
//        if (company == null) {
//            throw new EntityNotFoundException("Người dùng chưa có thông tin công ty");
//        }
//
//        // Filter job posts by company
//        Page<JobListingResponse> jobPosts = jobPostRepository.findAll();
//
//        return new PageResponse<>(jobPosts);
//    }

    public List<JobPostTitleResponse> getJobPostTitle() {
        return jobPostRepository.getJobPostTitleByCompany(authenticationUtil.getAuthenticatedUser().getCompany());
    }

    private JobPostTitleResponse convertToJobPostTitleResponse(JobPost jobPost) {
        return JobPostTitleResponse.builder()
                .id(jobPost.getId())
                .jobTitle(jobPost.getJobTitle())
                .build();
    }


    public JobListingResponse convertToJobListingResponse(JobPost jobPost) {
        return JobListingResponse.builder()
                .id(jobPost.getId())
                .jobTitle(jobPost.getJobTitle())
                .createDate(jobPost.getCreateDate())
                .endDate(jobPost.getEndDate())
                .appliedCount(jobPost.getAppliedCount())
                .status(jobPost.getStatus() != null ? jobPost.getStatus() : null)
                .statusEnum(jobPost.getStatusEnum() != null ? jobPost.getStatusEnum() : null)
                .build();
    }

    public Page<JobListingResponse> getAllJobListings(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        var jobListings = jobPostRepository.findAll(pageable);
        return jobListings.map(this::convertToJobListingResponse);
    }


    public Page<JobListActiveResponse> getJobListingsByStatus(String status, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);

        // Chuyển đổi từ String sang StatusEnum
        StatusEnum statusEnum;
        try {
            statusEnum = StatusEnum.fromString(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }

        // Gọi repository với StatusEnum
        Page<JobPost> jobListings = jobPostRepository.findByStatusEnum(statusEnum, pageable);
        System.out.println("Job" + jobListings.getTotalElements());

        // Xử lý in ra thông tin
        if (jobListings.hasContent()) {
            jobListings.getContent().forEach(job -> {
                System.out.println("Job Title: " + job.getJobTitle());
                System.out.println("Company Name: " + (job.getCompany() != null ? job.getCompany().getName() : "N/A")); // Kiểm tra null
                System.out.println("Status Enum: " + job.getStatusEnum());
                System.out.println("Applied Count: " + job.getAppliedCount());
                System.out.println("---------------------------------------------------");
            });
        } else {
            System.out.println("No job posts found with the specified status.");
        }

        // Chuyển đổi và trả về
        return convertToJobListActiveResponse(jobListings);
    }

    public Page<JobListActiveResponse> convertToJobListActiveResponse(Page<JobPost> jobListings) {
        return jobListings.map(jobPost -> {
            Company company = jobPost.getCompany();
            Long companyId = (company != null) ? company.getId() : null;
            String companyLogoUrl = (company != null) ? company.getLogo() : null;
            String companyName = (company != null) ? company.getName() : null;

            return new JobListActiveResponse(
                    jobPost.getId(),
                    jobPost.getJobTitle(),
                    jobPost.getCreateDate(),
                    jobPost.getEndDate(),
                    jobPost.getAppliedCount(),
                    jobPost.getStatusEnum(),
                    companyId,
                    companyLogoUrl,
                    companyName,
                    jobPost.getWorkType(),
                    jobPost.getStatus(),
                    jobPost.getCity()

            );
        });
    }

    public List<JobPost> findAll() {
        return jobPostRepository.findAll();
    }


    //để hiển thị trang chi tiết job post
    public JobPost findById(Long id) {
        return jobPostRepository.findById(id).orElse(null);
    }
}