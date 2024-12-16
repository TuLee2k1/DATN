package poly.com.dto.request.JobPost;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import poly.com.Enum.Exp;
import poly.com.Enum.JobLevel;
import poly.com.Enum.WorkType;



import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class JobPostRequest {

    private Long id;
    private Long userId;
    private String userEmail;
    @NotBlank(message = "Vui lòng nhập Tiêu đề công việc!")
    private String jobTitle; // Tên công việc

    @NotBlank(message = "Vui lòng nhập mô tả!")
    @Size(max = 2000, message = "Job description should not exceed 2000 characters")
    private String jobDescription; // Mô tả công việc

    @NotNull(message = "Vui lòng nhập số lượng")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity; // Số lượng tuyển

    @NotBlank(message = "Vui lòng nhập yêu cầu!")
    @Size(max = 2000, message = "Job requirements should not exceed 2000 characters")
    private String jobRequire; // Yêu cầu công việc

    @NotBlank(message = "Vui lòng nhập quyền lợi!")
    @Size(max = 2000, message = "Job benefits should not exceed 2000 characters")
    private String jobBenefit; // Quyền lợi

    @NotNull(message = "vui lòng chọn ngày kết thúc!")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date endDate; // Ngày kết thúc

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createDate; // Ngày kết thúc

    @NotNull(message = "Vui lòng nhập lương tối thiểu!")
    @Min(value = 1, message = "Quantity must be at least 1")
    private float minSalary; // Lương tối thiểu

    @NotNull(message = "Max salary is required")
    @Min(value = 1, message = "Vui lòng nhập lương tối đa!")
    private float maxSalary; // Lương tối đa

    @NotBlank(message = "Vui lòng chọn thành phố!")
    private String city; // Thành phố

    @NotBlank(message = "Vui lòng chọn quận!")
    private String district; // Quận

    @NotBlank(message = "Vui lòng nhập địa chỉ!")
    private String address; // Địa chỉ

    @NotNull(message = "Vui lòng chọn chuyên ngành!")
    private Long jobCategoryId; // ID của JobCategory

    @NotNull(message = "Vui lòng chọn vị trí!")
    private Long subCategoryIds; // Danh sách ID của SubCategory

    @NotNull(message = "Vui lòng chọn loại công việc!")
    private WorkType workType; // Hình thức làm việc

    @NotNull(message = "Vui lòng chọn cấp bậc!")
    private JobLevel jobLevel; // Cấp bậc công việc

    @NotNull(message = "Vui lòng chọn kinh nghiệm!")
    private Exp exp; // Kinh nghiệm

    private String companyName;



}