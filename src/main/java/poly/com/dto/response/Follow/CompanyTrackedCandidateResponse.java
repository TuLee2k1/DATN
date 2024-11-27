package poly.com.dto.response.Follow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import poly.com.Enum.StatusEnum;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompanyTrackedCandidateResponse {
    private String candidateName;        // Tên ứng viên
    private String candidateAvatar;      // Ảnh đại diện
    private String email;                // Email
    private String phone;                // Số điện thoại
    private int age;                     // Tuổi
    private String appliedJobTitle;      // Vị trí ứng tuyển
    private StatusEnum status;     // Trạng thái hồ sơ
    private String workExperience;       // Kinh nghiệm làm việc
    private String address;              // Địa chỉ
}