package poly.com.dto.request.Auth;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterCompanyRequest {
    @NotEmpty(message = "First name cannot be empty")
    @NotBlank(message = "First name cannot be blank")
    @Size(min = 2, max = 50, message = "First name should be between 2 and 50 characters")
    private String firstname;

    @NotEmpty(message = "Last name cannot be empty")
    @Size(min = 2, max = 50, message = "Last name should be between 2 and 50 characters")
    private String lastname;

    @Email(message = "Email should be valid")
    @NotEmpty(message = "Email cannot be empty")
    @NotBlank(message = "Email cannot be blank")
    @Column(unique = true)
    private String email;

    @NotEmpty(message = "Password cannot be empty")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 20, message = "Password should have at least 6 characters")
    private String password;

    @NotEmpty(message = "Password cannot be empty")
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 6, max = 20, message = "Password should have at least 6 characters")
    private String isPassword;

    @NotEmpty(message = "Company name cannot be empty")
    @NotBlank(message = "Company name cannot be blank")
    @Size(min = 10, max = 100, message = "Company name should be between 2 and 50 characters")
    private String companyName;

    @NotEmpty(message = "Company address cannot be empty")
    @NotBlank(message = "Company address cannot be blank")
    @Size(min = 5, max = 50, message = "Company address should be between 2 and 50 characters")
    private String city;

    @NotEmpty(message = "Company address cannot be empty")
    @NotBlank(message = "Company address cannot be blank")
    @Size(min = 5, max = 50, message = "Company address should be between 2 and 50 characters")
    private String district;

    @NotEmpty(message = "Company phone cannot be empty")
    @NotBlank(message = "Company phone cannot be blank")
    @Size(min = 10, max = 11, message = "Company phone should be between 10 and 11 characters")
    @Pattern(regexp = "0[0-9]{9,10}", message = "Company phone should be start with 0 and have 10-11 numbers")
    private String companyPhone;
}
