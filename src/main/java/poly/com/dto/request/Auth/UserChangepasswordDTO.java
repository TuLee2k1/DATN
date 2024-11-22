package poly.com.dto.request.Auth;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserChangepasswordDTO {

    @NotEmpty(message = "Old password is required")
    private String oldPassword;

    @NotEmpty(message = "New password is required")
    private String newPassword;

    @NotEmpty(message = "Confirm password is required")
    private String confirmPassword;

}
