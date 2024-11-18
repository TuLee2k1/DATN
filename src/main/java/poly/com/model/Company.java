    package poly.com.model;

    import jakarta.persistence.*;
    import lombok.*;
    import lombok.experimental.SuperBuilder;
    import poly.com.Enum.StatusEnum;


    @Entity
    @AllArgsConstructor
    @NoArgsConstructor
    @Setter
    @Getter
    @SuperBuilder
    @Table(name = "company")
    public class Company extends AbstractEntity {
        @Column(unique=true, name = "name")
        private String name; // tên công ty

        @Column(name = "phone") // số điện thoại công ty
        private String phone;

        @Column(name = "email") // email công ty
        private String email;

        @Column(name = "tax_code") // mã số thuế
        private String tax_code;

        @Column(name = "logo") // logo công ty
        private String logo;

        @Column(name = "website") // website công ty
        private String website;

        @Column(name = "address") // địa chỉ công ty
        private String address;

        @Column(name = "city") // thành phố
        private String city;

        @Column(name = "district") // quận
        private String district;

        @Column(name = "description") // mô tả công ty
        private String description;

        @Column(name = "business_type") // loại hình doanh nghiệp
        private String businessType;

        @Column(name = "inducstry") // ngành nghề
        private String inducstry;

        @Column(name = "year_established") // năm thành lập
        private int yearEstablished;

        @Column(name = "employee_count") // số lượng nhân viên
        private String employeeCount;


        @Enumerated(EnumType.STRING) // trạng thái công ty
        private StatusEnum status;

        @OneToOne
        @JoinColumn(name = "user_id")
        private User user;

        public String addressAndDistrict() {
            return  district + ", " + city;
        }


    }
