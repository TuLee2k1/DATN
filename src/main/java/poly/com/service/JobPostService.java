
package poly.com.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import poly.com.Enum.Exp;
import poly.com.Enum.JobLevel;
import poly.com.Enum.StatusEnum;
import poly.com.Enum.WorkType;
import poly.com.dto.response.JobPost.JobListActiveResponse;
import poly.com.repository.CompanyRepository;
import poly.com.util.AuthenticationUtil;

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
import java.util.stream.Collectors;

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

    public List<JobPost> getJobPostsByStatus(StatusEnum statusEnum) {
        return jobPostRepository.findByStatusEnum(statusEnum);
    }

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

    /*
     * @author: VuDD
     * @since: 11/29/2024 2:33 PM
     * @description:  Tao bài đăng việc làm
     * @update:
     *
     * */
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

    /*
     * @author: VuDD
     * @since: 11/29/2024 2:33 PM
     * @description:  Câp nhật thông tin bài đăng việc làm
     * @update:
     *
     * */
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


    /*
     * @author: VuDD
     * @since: 11/29/2024 2:33 PM
     * @description:  Lấy thông tin chi tiết bài đăng việc làm
     * @update:
     *
     * */
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
                .createDate(jobPost.getCreateDate())
                .build();

    }


    /*
     * @author: VuDD
     * @since: 11/29/2024 2:32 PM
     * @description:  Xoá bài đăng việc làm set trạng thái DELETED
     * @update:
     *
     * */
    public void deleteById(Long id) {
        JobPost jobPost = jobPostRepository.findById(id).orElseThrow(() -> new JobPostException("Không tìm thấy công " +
                "việc"));

        jobPost.setStatusEnum(StatusEnum.DELETED);
        jobPostRepository.save(jobPost);

    }

    /*
     * @author: VuDD
     * @since: 11/29/2024 2:32 PM
     * @description:  Ẩn bài đăng việc làm
     * @update:
     *
     * */
    public void disableView(Long id) {
        JobPost jobPost = jobPostRepository.findById(id).orElseThrow(() -> new JobPostException("Không tìm thấy công " +
                "việc"));

        jobPost.setStatus(JobPostStatus.Closed);
        jobPostRepository.save(jobPost);
    }

    /*
     * @author: VuDD
     * @since: 11/29/2024 2:32 PM
     * @description:  Bật bài đăng việc làm
     * @update:
     *
     * */
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

    /*
     * @author: VuDD
     * @since: 11/29/2024 2:31 PM
     * @description:  Lấy danh sách bài đăng việc làm theo công ty
     * @update:
     *
     * */
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


    /*
     * @author: VuDD
     * @since: 11/29/2024 2:31 PM
     * @description:  lấy danh sách bài đăng việc làm theo công ty
     * @update:
     *
     * */
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


    /*
     * @author: VuDD
     * @since: 11/29/2024 2:30 PM
     * @description:  Lấy danh sách bài đăng việc làm theo trạng thái
     * @update:
     *
     * */
    public Page<JobListActiveResponse> getJobListingsByStatus(String status, Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo - 1, pageSize, Sort.by(Sort.Direction.DESC, "createDate"));

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
            JobCategory jobCategory = jobPost.getJobCategory();
            Long companyId = (company != null) ? company.getId() : null;
            String companyLogoUrl = (company != null) ? company.getLogo() : null;
            String companyName = (company != null) ? company.getName() : null;
            Long jobCategoryId = (jobCategory != null) ? jobCategory.getId() : null;

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
                    jobPost.getCity(),
                    jobCategoryId,
                    jobPost.getExp(),
                    jobPost.getJobLevel()

            );
        });
    }

    public List<JobPost> findAll() {
        return jobPostRepository.findAll(Sort.by(Sort.Direction.DESC, "createDate"));
    }

    /*
     * @author: VuDD
     * @since: 11/29/2024 2:30 PM
     * @description:  Xóa bài đăng việc làm theo ID và trạng thái khỏi DB
     * @update:
     *
     * */
    public void deleteJobPostByStatusEnum(Long id) {
        try {
            // Kiểm tra tồn tại và trạng thái trước khi xóa
            JobPost jobPost = jobPostRepository.findById(id)

             .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy bài đăng việc làm với ID: " + id));

            // Kiểm tra điều kiện trạng thái
            if (jobPost.getStatusEnum() == StatusEnum.PENDING ||
             jobPost.getStatusEnum() == StatusEnum.REJECTED) {
                jobPostRepository.deleteJobPostByStatusEnum(id);
            } else {
                throw new IllegalStateException("Chỉ được xóa bài đăng ở trạng thái PENDING hoặc REJECTED");
            }
        } catch (Exception e) {
            // Ghi log lỗi
            System.out.println("Xóa bài đăng việc làm thất bại: " + e.getMessage());
            throw e;
        }
    }


    public List<JobPost> findByStatusEnum(StatusEnum statusEnum) {
        return jobPostRepository.findByStatusEnum(statusEnum); // Lấy bài đăng theo trạng thái
    }

    public Page<JobPost> getJobListingsAdmin(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10); // 10 là số lượng phần tử mỗi trang
        return jobPostRepository.findAll(pageable); // Lấy tất cả JobPosts với phân trang
    }

    public Page<JobPost> getJobListings(StatusEnum statusEnum, Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        return jobPostRepository.findByStatusEnum(statusEnum, pageable); // Lọc theo status và phân trang
    }

    public Page<JobPost> getJobListingsAdmin(Integer pageNo, String statusEnum,String jobTitle) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10, Sort.by(Sort.Direction.DESC, "createDate"));
        StatusEnum status = null;

        if (statusEnum != null && !statusEnum.isEmpty()) {
            try {
                status = StatusEnum.fromString(statusEnum); // Convert string to enum
            } catch (IllegalArgumentException e) {
                // Nếu trạng thái không hợp lệ, trả về tất cả job posts
            }
        }

        return jobPostRepository.findByAdmin(pageable, status,jobTitle);
    }



    //để hiển thị trang chi tiết job post
    public JobPost findById(Long id) {
        return jobPostRepository.findById(id).orElse(null);
    }


    public List<JobListActiveResponse> filterBySearchTerm(List<JobListActiveResponse> jobListings, String searchTerm) {
        return jobListings.stream()
                .filter(job -> job.getJobTitle().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());
    }

    public List<JobListActiveResponse> filterByJobType(List<JobListActiveResponse> jobListings, String jobType) {
       System.out.println("JobTypeService: "+jobType);
        return jobListings.stream()
                .filter(job -> job.getWorkType() == WorkType.valueOf(jobType)) // Nếu jobType là tên enum
                .collect(Collectors.toList());
    }

    public List<JobListActiveResponse> filterByLocation(List<JobListActiveResponse> jobListings, String location) {
        System.out.println("Filtering by location: " + location);
        return jobListings.stream()
                .filter(job -> {
                    String city = job.getCity();
                    System.out.println("Job City: " + city); // In ra tên thành phố
                    return city != null && city.toLowerCase().contains(location.toLowerCase());
                })
                .collect(Collectors.toList());
    }

    public List<JobListActiveResponse> filterByJobCategory(List<JobListActiveResponse> jobListings, Long jobCategory) {
        System.out.println("Filtering by jobCategory: " + jobCategory);

// In ra tất cả các job và jobCategoryId của chúng
        jobListings.forEach(job -> {
            System.out.println("Job ID: " + job.getId() + ", Job Category ID: " + job.getJobCategoryId());
        });

        return jobListings.stream()
                .filter(job -> {
                    System.out.println("List: " + job.getJobCategoryId());
                    Long jobcate = job.getJobCategoryId();
                    System.out.println("Job Category: " + jobcate); // In ra tên thành phố
                    return jobcate != null && jobcate.equals(jobCategory);
                })
                .collect(Collectors.toList());
    }
    public List<JobListActiveResponse> filterByExp(List<JobListActiveResponse> jobListings, Exp exp) {
       System.out.println("Filtering by exp: " + exp);
        return jobListings.stream()
                .filter(job -> {
                    System.out.println("List: " + job.getExp());
                    Exp jobExp = job.getExp();
                    System.out.println("Job Exp: " + jobExp); // In ra tên thành phố
                    return jobExp != null && jobExp.equals(exp);
                })
                .collect(Collectors.toList());
    }
    public List<JobListActiveResponse> filterByWorkType(List<JobListActiveResponse> jobListings, WorkType workType) {
        System.out.println("Filtering by worktype: " + workType);
        return jobListings.stream()
                .filter(job -> {
                    System.out.println("List: " + job.getWorkType());
                    WorkType jobWorkType = job.getWorkType();
                    System.out.println("Job WorkType: " + jobWorkType); // In ra tên thành phố
                    return jobWorkType != null && jobWorkType.equals(workType);
                })
                .collect(Collectors.toList());
    }
    public List<JobListActiveResponse> filterByJobLevel(List<JobListActiveResponse> jobListings, JobLevel jobLevel) {
        System.out.println("Filtering by jobLevel: " + jobLevel);
        return jobListings.stream()
                .filter(job -> {
                    System.out.println("List: " + job.getJobLevel());
                    JobLevel jobjobLevel = job.getJobLevel();
                    System.out.println("Job Level: " + jobjobLevel); // In ra tên thành phố
                    return jobjobLevel != null && jobjobLevel.equals(jobLevel);
                })
                .collect(Collectors.toList());
    }

//    public Page<JobListActiveResponse> getSearchJobListingsByStatus(String status, String searchTerm, String jobType, String location, Integer pageNo, Integer pageSize) {
//        Pageable pageable = PageRequest.of(pageNo - 1, pageSize);
//
//        // Chuyển đổi từ String sang StatusEnum
//        StatusEnum statusEnum;
//        try {
//            statusEnum = StatusEnum.fromString(status);
//        } catch (IllegalArgumentException e) {
//            throw new IllegalArgumentException("Invalid status value: " + status);
//        }
//
//        // Gọi repository với StatusEnum
//        Page<JobPost> jobListings = jobPostRepository.findByStatusEnum(statusEnum, pageable);
//        System.out.println("Job" + jobListings.getTotalElements());
//
//        // Chuyển đổi danh sách JobPost thành danh sách JobListActiveResponse
//        List<JobListActiveResponse> jobListActiveResponses = (List<JobListActiveResponse>) convertToJobListActiveResponse((Page<JobPost>) jobListings.getContent());
//
//        // Lọc theo searchTerm, jobType và location
//        if (searchTerm != null && !searchTerm.isEmpty()) {
//            jobListActiveResponses = filterBySearchTerm(jobListActiveResponses, searchTerm);
//        }
//        if (jobType != null && !jobType.isEmpty()) {
//            jobListActiveResponses = filterByJobType(jobListActiveResponses, jobType);
//        }
//        if (location != null && !location.isEmpty()) {
//            jobListActiveResponses = filterByLocation(jobListActiveResponses, location);
//        }
//
//        // Tạo một Page mới từ danh sách đã lọc
//        return createPageFromList(jobListActiveResponses, pageable);
//    }
//
//    private Page<JobListActiveResponse> createPageFromList(List<JobListActiveResponse> list, Pageable pageable) {
//        int totalElements = list.size();
//        int start = Math.toIntExact(pageable.getOffset());
//        int end = Math.min((start + pageable.getPageSize()), totalElements);
//        List<JobListActiveResponse> pagedList = list.subList(start, end);
//
//        return new PageImpl<>(pagedList, pageable, totalElements);
//    }
}

