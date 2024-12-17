package poly.com.dto.response.Follow;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
public class JobPostFollowResponse {
    private Long jobPostId;
    private String companyLogo;      // Logo công ty
    private String jobTitle;         // Tiêu đề công việc
    private String companyName;      // Tên công ty
    private float minSalary;         // Lương tối thiểu
    private float maxSalary;         // Lương tối đa
    private String city;             // Thành phố
}
