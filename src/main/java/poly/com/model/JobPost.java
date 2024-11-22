package poly.com.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.autoconfigure.batch.BatchProperties;

import java.util.Date;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name= "JobPost")
public class JobPost extends AbstractEntity{
    @Column(name = "jobTitle")
    private String jobTitle;

    @Column(name = "jobDescription", length = 2000)
    private String jobDescription;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "jobRequire", length = 2000)
    private String jobRequire;

    @Temporal(TemporalType.DATE)
    @Column(name = "createDate")
    private Date createDate;

    @Column(name = "minSalary")
    private float minSalary;

    @Column(name = "maxSalary")
    private float maxSalary;

    @Column(name = "endDate")
    private Date endDate;


    @Column(name = "city")
    private String city;

    @ManyToOne
    @JoinColumn(name = "jobCategory_id")
    private  JobCategory jobCategory_id;

   @ManyToOne
   @JoinColumn(name = "company_id")
   private Company company_id;

    @Column(name = "status")
    private JobPostStatus status;


    @PreUpdate
    public void preUpdate() {
        endDate = new Date();
    }


}
