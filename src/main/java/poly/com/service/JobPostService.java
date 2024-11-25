package poly.com.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import poly.com.Enum.StatusEnum;
import poly.com.util.AuthenticationUtil;
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

        // Không cần cập nhật company, jobCategory, subCategory nếu không thay đổi

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

    public JobPostResponse getJobPost (Long id) {
        var jobPost = jobPostRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy công việc"));
        return JobPostResponse.fromEntity(jobPost);
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