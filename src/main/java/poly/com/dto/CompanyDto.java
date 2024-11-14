package poly.com.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * DTO for {@link poly.com.model.Company}
 */
@Data
public class CompanyDto implements Serializable {
    private Long id;
    @NotEmpty(message = "Company name is require")
    private String name;

    @NotEmpty(message = "Company address is require")
    private String address;

    @NotEmpty(message = "Company phone is require")
    private String phone;

    @NotEmpty(message = "Company email is require")
    private String email;

    @NotEmpty(message = "Company taxcode is require")
    private String tax_code;


    private String logo;

    @JsonIgnore
    private MultipartFile logoFile;

}