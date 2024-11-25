package poly.com.dto.response.JobPost;

import lombok.Getter;
import lombok.Setter;
import poly.com.Enum.StatusEnum;
import poly.com.Enum.WorkType;
import poly.com.model.JobPostStatus;

import java.util.Date;
@Getter
@Setter
public class JobListActiveResponse {
    private Long id;
    private String jobTitle;
    private Date createDate;
    private Date endDate;
    private Integer appliedCount;
    private StatusEnum statusEnum;
    private Long companyId;
    private String companyLogoUrl;
    private String companyName;
    private WorkType workType;
    private JobPostStatus status;
    private String City;

    public JobListActiveResponse(Long id, String jobTitle, Date createDate, Date endDate, Integer appliedCount, StatusEnum statusEnum, Long companyId, String companyLogoUrl, String companyName, WorkType workType, JobPostStatus status, String city) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.createDate = createDate;
        this.endDate = endDate;
        this.appliedCount = appliedCount;
        this.statusEnum = statusEnum;
        this.companyId = companyId;
        this.companyLogoUrl = companyLogoUrl;
        this.companyName = companyName;
        this.workType = workType;
        this.status = status;
        City = city;
    }


// Getters and Setters
}