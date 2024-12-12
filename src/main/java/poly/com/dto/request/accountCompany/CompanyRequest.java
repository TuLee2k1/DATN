package poly.com.dto.request.accountCompany;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CompanyRequest {

    private Long id; // ID của công ty

    @NotEmpty(message = "Tên công ty không được để trống")
    private String companyName; // Tên công ty

    @NotEmpty(message = "Số điện thoại không được để trống")
    private String phone; // Số điện thoại

    @NotEmpty(message = "Email công ty không được để trống")
    private String companyEmail;

    private String tax_code; // Mã số thuế

    @JsonIgnore
    private MultipartFile logoFile; // File logo được tải lên (nếu có)

    private String website; // Website công ty

    private String address; // Địa chỉ

    private String city; // Thành phố

//    private String district; // Quận

    private String description; // Mô tả công ty

//    private String businessType; // Loại hình doanh nghiệp

//    private String industry; // Ngành nghề

//    private Integer yearEstablished; // Năm thành lập

    private String employeeCount; // Số lượng nhân viên

    private MultipartFile businessLicense;// Giấy phép kinh doanh

    @NotNull(message = "Job category ID is required")
    private Long jobCategoryId; // ID của JobCategory

    private Long userId; // ID của người dùng liên kết
}


