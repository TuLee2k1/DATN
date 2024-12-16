package poly.com.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import poly.com.Enum.Exp;
import poly.com.Enum.WorkType;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileSearchResult {
    private Long id;
    private String name;
    private int age;
    private String desiredLocation;
    private BigDecimal desiredSalary;
    private String address;
    private long totalCount;
    private WorkType workType;
    private Exp exp;
    private String email;
    private String phone;
    private boolean isFollowed;
    private Date followDate;

    public ProfileSearchResult(Long id, String name, int age, String desiredLocation, BigDecimal desiredSalary,
                               String address, long totalCount, WorkType workType, Exp exp) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.desiredLocation = desiredLocation;
        this.desiredSalary = desiredSalary;
        this.address = address;
        this.totalCount = totalCount;
        this.workType = workType;
        this.exp = exp;
    }

    public ProfileSearchResult(Long id, String name, int age, String desiredLocation, BigDecimal desiredSalary,
                               String address,  Exp exp, String email, String phone,Date followDate) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.desiredLocation = desiredLocation;
        this.desiredSalary = desiredSalary;
        this.address = address;
        this.exp = exp;
        this.email = email;
        this.phone = phone;
        this.followDate = followDate;
    }
}
