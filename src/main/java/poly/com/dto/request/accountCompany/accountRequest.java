package poly.com.dto.request.accountCompany;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class accountRequest {

    private Long   id;

    private MultipartFile fileLogo;

    private String Name;

    private String phone;


    private String address;

}
