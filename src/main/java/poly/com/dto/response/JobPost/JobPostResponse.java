package poly.com.dto.response.JobPost;

import lombok.Builder;
import lombok.Data;
import poly.com.Enum.StatusEnum;
import poly.com.model.JobPost;
import poly.com.model.JobPostStatus;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class JobPostResponse {
    private Long id;
    private String jobTitle;
    private String jobDescription;
    private Integer quantity;
    private String jobRequire;
    private String jobBenefit;
    private Date createDate;
    private Date endDate;
    private float minSalary;
    private float maxSalary;
    private String city;
    private String district;
    private String address;
    private String companyName;
    private String jobCategoryName;
    private String subCategoryName;
    private JobPostStatus status;
    private StatusEnum statusEnum;

    public static JobPostResponse fromEntity(JobPost jobPost) {
        return JobPostResponse.builder()
                .id(jobPost.getId())
                .jobTitle(jobPost.getJobTitle())
                .jobDescription(jobPost.getJobDescription())
                .quantity(jobPost.getQuantity())
                .jobRequire(jobPost.getJobRequire())
                .jobBenefit(jobPost.getJobBenefit())
                .createDate(jobPost.getCreateDate())
                .endDate(jobPost.getEndDate())
                .minSalary(jobPost.getMinSalary())
                .maxSalary(jobPost.getMaxSalary())
                .city(jobPost.getCity())
                .district(jobPost.getDistrict())
                .address(jobPost.getAddress())
                .companyName(jobPost.getCompany() != null ? jobPost.getCompany().getName() : null)
                .jobCategoryName(jobPost.getJobCategory() != null ? jobPost.getJobCategory().getCategoryName() : null)
                .subCategoryName(jobPost.getSubCategory() != null ? jobPost.getSubCategory().getSubCategoryName() : null)
                .status(jobPost.getStatus())
                .statusEnum(jobPost.getStatusEnum())
                .build();
    }

    public static List<JobPostResponse> fromEntities (List<JobPost> jobPosts) {
        return jobPosts.stream().map(JobPostResponse::fromEntity).toList();
    }
}