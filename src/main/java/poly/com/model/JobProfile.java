
package poly.com.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.Date;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
@Table(name= "jobProfiles")
public class JobProfile extends AbstractEntity {

    @Column(name = "fileCV")
    private String fileCV;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String phoneNumber;

    @Temporal(TemporalType.DATE)
    @Column(name = "dateApply")
    private Date dateApply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "jobPost_id")
    private JobPost jobPost;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile;

}
