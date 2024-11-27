package poly.com.dto.request.JobPost;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class JobPostTitleResponse
{
    private Long id;
    private String jobTitle;

}