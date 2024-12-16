package poly.com.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AppliedJobRequest {
    private long id; // JobProfile ID
    private String jobTitle; // JobPost
    private String workType; // JobPost
    private String companyName; // Company
    private float minSalary; // JobPost
    private float maxSalary; // JobPost
    private String status;
    private String logo;
    private String location;
}
