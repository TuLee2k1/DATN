package poly.com.dto.response;

import lombok.*;
import poly.com.Enum.StatusEnum;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AppliedJobResponse {
    private Long jobPostId;
    private StatusEnum status;
    private String jobTitle;
    private String companyName;
    private Date dateApply;
    private String workType;
    private Float minSalary;
    private Float maxSalary;
    private String city;
    private String companyLogoUrl;
}