package poly.com.model;

import jakarta.persistence.Column;

import java.util.Date;

public class Profile extends AbstractEntity{
    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "phone")
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "sex")
    private String sex;

    @Column(name = "dateOfBirth")
    private Date dateOfBirth;

}
