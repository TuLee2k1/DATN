package poly.com.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "schools")
public class School extends AbstractEntity{
    @Column(name = "schoolName")
    private String schoolName;

    @Column(name = "degree")
    private String degree;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "startDate")
    private LocalDate startDate;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "endDate")
    private LocalDate endDate;

    @Column(name = "GPA")
    private float GPA;

    @ManyToOne
    @JoinColumn(name = "profile_id")
    private Profile profile_id;

}
