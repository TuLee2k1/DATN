package poly.com.dto.response.JobPost;

import lombok.Getter;
import lombok.Setter;
import poly.com.Enum.Exp;
import poly.com.Enum.JobLevel;
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
    private Long jobCategoryId;
    private Exp exp;
    private JobLevel jobLevel;
    private Float maxSalary;
    private Integer Salary;

    public JobListActiveResponse(Long id, String jobTitle, Date createDate, Date endDate, Integer appliedCount, StatusEnum statusEnum, Long companyId, String companyLogoUrl, String companyName, WorkType workType, JobPostStatus status, String city, Long jobCategoryId, Exp exp, JobLevel jobLevel, Float maxSalary, float Salary) {
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
        this.jobCategoryId = jobCategoryId;
        this.exp = exp;
        this.jobLevel = jobLevel;
        this.maxSalary = maxSalary;
    }

    public JobListActiveResponse() {

    }


// Getters and Setters
}