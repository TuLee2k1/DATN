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
@Table(name= "jobProfiles")
public class JobProfile extends AbstractEntity {

    @Column(name = "fileCV")
    private String fileCV;

    @Temporal(TemporalType.DATE)
    @Column(name = "dateApply")
    private Date dateApply;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private JobPost job;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

}
