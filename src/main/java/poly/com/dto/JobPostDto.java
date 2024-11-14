package poly.com.dto;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;
import poly.com.model.JobPost;
import poly.com.model.JobPostStatus;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link JobPost}
 */
@Data

public class JobPostDto implements Serializable {
    private Long id;

    @NotEmpty(message = "Tittle cannot be empty")
    private String jobTitle;

    @NotEmpty(message = "Desc cannot be empty")
    private String jobDescription;

    @NotNull
    private Integer quantity;

    @NotEmpty(message = "Require cannot be empty")
    private String jobRequire;

    @Temporal(TemporalType.DATE)
    private Date createDate;



    private float minSalary;

    private float maxSalary;


    private Date endDate;


    @NotEmpty(message = "City name is require")
    private String city;



    JobPostStatus status;
}