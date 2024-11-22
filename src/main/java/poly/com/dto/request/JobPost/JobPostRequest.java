package poly.com.dto.request.JobPost;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import poly.com.Enum.StatusEnum;
import poly.com.model.JobPostStatus;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class JobPostRequest {

    private Long userId;
    private String userEmail;
    @NotBlank(message = "Job title is required")
    private String jobTitle; // Tên công việc

    @NotBlank(message = "Job description is required")
    @Size(max = 2000, message = "Job description should not exceed 2000 characters")
    private String jobDescription; // Mô tả công việc

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity; // Số lượng tuyển

    @NotBlank(message = "Job requirements are required")
    @Size(max = 2000, message = "Job requirements should not exceed 2000 characters")
    private String jobRequire; // Yêu cầu công việc

    @NotBlank(message = "Job benefits are required")
    @Size(max = 2000, message = "Job benefits should not exceed 2000 characters")
    private String jobBenefit; // Quyền lợi

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createDate; // Ngày tạo

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate; // Ngày kết thúc

    @NotNull(message = "Min salary is required")
    @Min(value = 0, message = "Min salary must be greater than or equal to 0")
    private float minSalary; // Lương tối thiểu

    @NotNull(message = "Max salary is required")
    @Min(value = 0, message = "Max salary must be greater than or equal to 0")
    private float maxSalary; // Lương tối đa

    @NotBlank(message = "City is required")
    private String city; // Thành phố

    @NotBlank(message = "District is required")
    private String district; // Quận

    @NotBlank(message = "Address is required")
    private String address; // Địa chỉ

    @NotNull(message = "Job category ID is required")
    private Long jobCategoryId; // ID của JobCategory

    @NotNull(message = "SubCategory IDs are required")
    private Long subCategoryIds; // Danh sách ID của SubCategory


}