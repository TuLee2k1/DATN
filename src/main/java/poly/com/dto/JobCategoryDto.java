package poly.com.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;

/**
 * DTO for {@link poly.com.model.JobCategory}
 */
@Data
public class JobCategoryDto implements Serializable {

    private Long id;

    @NotEmpty(message = "JobCategory is require!")
    private String categoryName;
}