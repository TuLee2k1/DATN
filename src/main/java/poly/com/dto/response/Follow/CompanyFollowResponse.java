package poly.com.dto.response.Follow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyFollowResponse {
    private String companyLogo;      // Logo công ty
    private String companyName;      // Tên công ty
    private String address;          // Địa chỉ công ty
    private String employeeCount;    // Số lượng nhân viên (đã điều chỉnh thành String)
    private int jobPostCount;        // Số lượng bài đăng tuyển dụng
}
