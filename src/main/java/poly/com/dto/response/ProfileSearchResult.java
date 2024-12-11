package poly.com.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import poly.com.Enum.WorkType;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileSearchResult {
    private Long id;
    private String name;
    private int age;
    private String desiredLocation;
    private BigDecimal desiredSalary;
    private String address;
    private long totalCount;
    private WorkType workType;

}
