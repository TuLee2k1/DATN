package poly.com.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;
import poly.com.Enum.WorkType;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "profiles")

public class Profile extends AbstractEntity{
    @Column(name = "name") // tên
    private String name;

    @Column(name = "email") // email trên hồ sơ
    private String email;

    @Column(name = "phone") // số điện thoại
    private String phone;

    @Column(name = "address") // địa chỉ
    private String address;

    @Column(name = "sex") // giới tính
    private String sex;

    @Column(name = "dateOfBirth") // ngày sinh
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @Column(name = "logo", length = 100) // ảnh đại diện
    private String logo;

    @Column(name = "careerGoals")
    private String careerGoals; // mục tiêu nghề nghiệp

    @Column(name = "desiredSalary")
    private Float desiredSalary; // mức lương mong muốn

    @Enumerated(EnumType.STRING)
    @Column(name = "workType")
    private WorkType workType; // loại công việc

    @ManyToOne
    @JoinColumn(name = "user_id") // khóa ngoại liên kết đến User
    private User user_id;

    @OneToMany(mappedBy = "profile", fetch = FetchType.LAZY)
    private List<Experience> experiences; // danh sách kinh nghiệm làm việc

}
