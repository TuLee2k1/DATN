package poly.com.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
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

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "startDate")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "endDate")
    private LocalDate endDate;

    @Column(name = "GPA") // điểm trung bình
    private float GPA;

    @ManyToOne
    @JoinColumn(name = "profile_id") // khóa ngoai lien ket voi bang profile
    private Profile profile_id;

}
