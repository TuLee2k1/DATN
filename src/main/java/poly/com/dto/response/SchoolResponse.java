package poly.com.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;


import java.time.LocalDate;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class SchoolResponse {
    Long id;
    String schoolName;
    String degree;
    LocalDate startDate;
    LocalDate endDate;
    float GPA;
    Long profile_id;

}
