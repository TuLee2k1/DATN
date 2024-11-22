package poly.com.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class ExperienceDTO implements Serializable {
    private Long id;

    @NotEmpty(message = "Job Title is required!")
    private String jobTitle;

    @NotEmpty(message = "Job Description is required!")
    private String jobDescription;

    @NotEmpty(message = "Company Name is required!")
    private String companyName;

    private Date startDate;
    private Date endDate;
    private Long profileId;
}
