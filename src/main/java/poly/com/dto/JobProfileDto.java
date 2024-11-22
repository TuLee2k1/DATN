package poly.com.dto;

import lombok.Data;
import lombok.Value;
import poly.com.model.JobPost;
import poly.com.model.Profile;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link poly.com.model.JobProfile}
 */
@Data
public class JobProfileDto implements Serializable {
    private Long id;
    private String fileCV;
}