package poly.com.dto.request;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class profileRequest {
    private Long id;

    @NotEmpty(message = "name is require!")
    private String name;

    @NotEmpty(message = "email is require!")
    private String email;

    @NotEmpty(message = "phone is require!")
    private String phone;

    @NotEmpty(message = "address is require!")
    private String address;

    @NotEmpty(message = "sex is require!")
    private String sex;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String logo;

    @JsonIgnore
    private MultipartFile logoFile;
}
