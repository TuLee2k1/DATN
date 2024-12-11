package poly.com.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import poly.com.Enum.Exp;
import poly.com.Enum.WorkType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Table(name = "profiles")

public class Profile extends AbstractEntity{
    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "desiredLocation")
    private String desiredLocation;

    @Column(name = "desired_salary")
    private BigDecimal desired_salary;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "sex")
    private String sex;

    @Column(name = "dateOfBirth")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Column(name = "logo", length = 100)
    private String logo;

    @Enumerated(EnumType.STRING)
    @Column(name = "workType")
    private WorkType workType;

    @Enumerated(EnumType.STRING)
    @Column(name = "exp")
    private Exp exp;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user_id;

}
