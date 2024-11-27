package poly.com.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name= "experience")
public class Experience extends AbstractEntity{
    @Column(name = "jobTitle")
    private String jobTitle;

    @Column(name = "jobDescription") // mô tả công việc
    private String jobDescription;

    @Column(name = "companyName") // tên công ty
    private String companyName;

    @Column(name = "startDate") // ngày bắt đầu làm việc
    private Date startDate;

    @Column(name = "endDate") // ngày kết thúc làm việc
    private Date endDate;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;
}
