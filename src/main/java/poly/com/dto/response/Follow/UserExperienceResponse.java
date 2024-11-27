package poly.com.dto.response.Follow;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserExperienceResponse {
    private String name;
    private int age;
    private Double maxSalary;
    private Double minSalary;
    private String experience;
    private String cityAddress;
}
