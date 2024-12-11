package poly.com.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import poly.com.Enum.EducationLevel;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "schools")
public class School extends AbstractEntity{
    @Column(name = "schoolName") // tên trường
    private String schoolName;

    @Column(name = "degree") // bằng cấp
    @Enumerated(EnumType.STRING)
    private EducationLevel degree;

    @JsonFormat(pattern = "dd-MM-yyyy")// ngày bắt đầu học
    @Column(name = "startDate")
    private LocalDate startDate;

    @JsonFormat(pattern = "dd-MM-yyyy") // ngày kết thúc học
    @Column(name = "endDate")
    private LocalDate endDate;

    @Column(name = "GPA") // điểm trung bình
    private float GPA;

    @ManyToOne
    @JoinColumn(name = "profile_id") // khóa ngoai lien ket voi bang profile
    private Profile profile_id;

}
