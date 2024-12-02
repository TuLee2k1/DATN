package poly.com.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
@Table(name = "follows")
public class Follow extends AbstractEntity {

    @Column(name = "user_id", nullable = false)
    private Long userId; // ID của người dùng theo dõi

    @Column(name = "company_id")
    private Long companyId; // ID của công ty được theo dõi

    @Column(name = "job_post_id")
    private Long jobPostId; // ID của bài đăng được theo dõi

    @Temporal(TemporalType.DATE)
    @Column(name = "follow_date")
    private Date followDate;

    @PrePersist
    public void prePersist() {
        followDate = new Date();
    }
}
