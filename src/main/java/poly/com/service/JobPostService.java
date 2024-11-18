package poly.com.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import poly.com.Enum.StatusEnum;
import poly.com.Util.AuthenticationUtil;
import poly.com.dto.request.JobPost.JobPostRequest;
import poly.com.dto.request.JobPost.JobPostTitleResponse;
import poly.com.dto.request.PageRequestDTO;
import poly.com.dto.response.JobPost.JobListingResponse;
import poly.com.dto.response.JobPost.JobPostResponse;
import poly.com.dto.response.PageResponse;
import poly.com.exception.JobPostException;
import poly.com.model.*;
import poly.com.repository.CompanyRepository;
import poly.com.repository.JobCategoryRepository;
import poly.com.repository.JobPostRepository;
import poly.com.repository.SubCategoryRepository;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class JobPostService {
    private final JobPostRepository jobPostRepository;
    private final JobCategoryRepository jobCategoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final AuthenticationUtil authenticationUtil;

    public JobPostResponse createJobPost(JobPostRequest request) {
        User authenticatedUser = authenticationUtil.getAuthenticatedUser();

        Company company = authenticatedUser.getCompany();
        if (company == null) {
            throw new RuntimeException("Người dùng chưa có thông tin công ty");
        }

        // Validate dates
        if (request.getEndDate().before(request.getCreateDate())) {
            throw new RuntimeException("Ngày kết thúc phải sau ngày tạo");
        }

        // Validate salary
        if (request.getMaxSalary() < request.getMinSalary()) {
            throw new RuntimeException("Lương tối đa phải lớn hơn lương tối thiểu");
        }

        var jobCategory = jobCategoryRepository.findById(request.getJobCategoryId())
         .orElseThrow(() -> new RuntimeException("Không tìm thấy ngành nghề"));

        SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryIds())
         .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên ngành"));

        var jobPost = JobPost.builder()
         .jobTitle(request.getJobTitle())
         .jobDescription(request.getJobDescription())
         .quantity(request.getQuantity())
         .jobRequire(request.getJobRequire())
         .jobBenefit(request.getJobBenefit())
         .createDate(request.getCreateDate())
         .endDate(request.getEndDate())
         .minSalary(request.getMinSalary())
         .maxSalary(request.getMaxSalary())
         .city(request.getCity())
         .district(request.getDistrict())
         .address(request.getAddress())
         .company(company)
         .jobCategory(jobCategory)
         .subCategory(subCategory)
         .status(JobPostStatus.Open) // trạng thái mặc định cho hiển thị trên view
         .statusEnum(StatusEnum.PENDING) // Chờ duyệt
         .build();

        var savedJobPost = jobPostRepository.save(jobPost);
        return JobPostResponse.fromEntity(savedJobPost);
    }


    public JobPostResponse getJobPost (Long id) {
        var jobPost = jobPostRepository.findById(id)
         .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy công việc"));
        return JobPostResponse.fromEntity(jobPost);
    }

    public JobPostResponse updateJobPost(Long id, JobPostRequest request) {
        var jobPost = jobPostRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Không tìm thấy công việc"));

        // Validate dates
        if (request.getEndDate().before(request.getCreateDate())) {
            throw new RuntimeException("Ngày kết thúc phải sau ngày tạo");
        }

        // Validate salary
        if (request.getMaxSalary() < request.getMinSalary()) {
            throw new RuntimeException("Lương tối đa phải lớn hơn lương tối thiểu");
        }

        var jobCategory = jobCategoryRepository.findById(request.getJobCategoryId()).orElseThrow(() -> new RuntimeException("Không tìm thấy ngành nghề"));

        SubCategory subCategory = subCategoryRepository.findById(request.getSubCategoryIds()).orElseThrow(() -> new RuntimeException("Không tìm thấy chuyên ngành"));

        var updatedJobPost = JobPost.builder()
         .id(jobPost.getId())
         .jobTitle(request.getJobTitle())
         .jobDescription(request.getJobDescription())
         .quantity(request.getQuantity())
         .jobRequire(request.getJobRequire())
         .jobBenefit(request.getJobBenefit())
         .createDate(request.getCreateDate())
         .endDate(request.getEndDate())
         .minSalary(request.getMinSalary())
         .maxSalary(request.getMaxSalary())
         .city(request.getCity())
         .district(request.getDistrict())
         .address(request.getAddress())
         .company(jobPost.getCompany())
         .jobCategory(jobCategory)
         .subCategory(subCategory)
         .status(jobPost.getStatus())
         .statusEnum(jobPost.getStatusEnum()).build();
        var savedJobPost = jobPostRepository.save(updatedJobPost);
        return JobPostResponse.fromEntity(savedJobPost);

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

    public Page<JobListingResponse> getJobListings(Integer  pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10 );
        var jobListings = jobPostRepository.findAll(pageable);
        return jobListings.map(this::convertToJobListingResponse);
    }

    public PageResponse<JobListingResponse> getJobListings(String jobTitle, StatusEnum statusEnum, Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10);

        Page<JobPost> jobPosts = jobPostRepository.findAllByJobTitleContainingAndStatusEnum(jobTitle, statusEnum, pageable);

        Page<JobListingResponse> responsePage = jobPosts.map(this::convertToJobListingResponse);

        return new PageResponse<>(responsePage);
    }

    public List<JobPostTitleResponse> getJobPostTitle() {
        return jobPostRepository.getJobPostTitle();
    }

    private JobPostTitleResponse convertToJobPostTitleResponse (JobPost jobPost) {
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


}