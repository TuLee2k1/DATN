package poly.com.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import poly.com.Enum.EducationLevel;
import poly.com.model.Profile;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResumeRequest {
    private Long id;

    @NotEmpty(message = "name is required!")
    private String name;

    @NotEmpty(message = "email is required!")
    private String email;

    @NotEmpty(message = "phone is required!")
    private String phone;

    @NotEmpty(message = "address is required!")
    private String address;

    @NotEmpty(message = "sex is required!")
    private String sex;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String schoolName;

    private EducationLevel degree;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private float GPA;

    private Profile profile_id;

    private String jobTitle;

    private String jobDescription;

    private String companyName;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date start;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date end;
}