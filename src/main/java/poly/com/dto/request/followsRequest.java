package poly.com.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class followsRequest {
    private long id;
    private String companyName;
    private String jobTitle;
    private float minSalary;
    private float maxSalary;
    private String logo;



}
