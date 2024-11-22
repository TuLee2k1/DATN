package poly.com.model;

import jakarta.persistence.*;
import lombok.*;

import lombok.experimental.SuperBuilder;
import poly.com.Enum.StatusEnum;

import java.util.Date;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
@Table(name= "JobPost")
public class JobPost extends AbstractEntity{

    @Column(name = "jobTitle") // Tên công việc
    private String jobTitle;

    @Column(name = "jobDescription", length = 2000) // Mô tả công việc
    private String jobDescription;

    @Column(name = "quantity") // Số lượng tuyển
    private Integer quantity;

    @Column(name = "jobRequire", length = 2000) // Yêu cầu công việc
    private String jobRequire;

    @Column(name = "jobBenefit", length = 2000) // Quyền lợi
    private String jobBenefit;

    @Temporal(TemporalType.DATE)
    @Column(name = "createDate") // Ngày tạo
    private Date createDate;

    @Temporal(TemporalType.DATE)
    @Column(name = "endDate") // Ngày kết thúc
    private Date endDate;

    @Column(name = "minSalary") // Lương tối thiểu
    private float minSalary;

    @Column(name = "maxSalary") // Lương tối đa
    private float maxSalary;

    @Column(name = "city") // Thành phố
    private String city;

    @Column(name = "district") // Quận
    private String district;

    @Column(name = "address") // Địa chỉ
    private String address;


    @Column(name = "applied_count")
    private Integer appliedCount = 0;


    @ManyToOne
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne
    @JoinColumn(name = "job_category_id")
    private JobCategory jobCategory;

    @ManyToOne
    @JoinColumn(name = "sub_category_id")
    private SubCategory subCategory;

    @OneToMany(mappedBy = "jobPost",fetch = FetchType.LAZY) // khóa ngoại liên kết đến JobProfile np CV
    private List<JobProfile> jobProfiles;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private JobPostStatus status; // Trạng thái hiển thị view cho người dùng

    @Enumerated(EnumType.STRING)
    @Column(name = "statusEnum")
    private StatusEnum statusEnum; // Trạng thái admin  duyet

    @PreUpdate
    public void preUpdate() {
        createDate = new Date();
    }

}