package poly.com.dto.response;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SavedJobPostResponse {
    private Long id;
    private String companyName;
    private String jobTitle;
    private float minSalary;
    private float maxSalary;
    private String logo;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date followDate;
}