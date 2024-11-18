package poly.com.dto.response.JobPost;

import lombok.*;
import poly.com.Enum.StatusEnum;
import poly.com.model.JobPost;
import poly.com.model.JobPostStatus;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
public class JobListingResponse {
    private Long id;
    private String jobTitle;
    private Date createDate;
    private Date endDate;
    private Integer appliedCount;
    private JobPostStatus status;
    private StatusEnum statusEnum;
}
