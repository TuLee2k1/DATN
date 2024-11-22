package poly.com.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "staff")
public class Staff extends AbstractEntity {
    @Column(name = "fullName")
    private String fullName;

    @Column(name = "phoneNumber")
    private String phoneNumber;

    @Column(name = "position")
    private String position;

    @Column(name = "address")
    private String address;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "dob")
    private LocalDate dob;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
