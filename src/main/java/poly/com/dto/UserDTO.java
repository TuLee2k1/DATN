package poly.com.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import poly.com.Enum.RoleType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    @NotNull(message = "Email cannot be null")
    @Email(message = "Email should be valid")
    @Pattern(
    regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$",
    message = "Email should match the pattern"
    )
    private String email;

    @NotNull(message = "Name cannot be null")
    private String name;

    @NotNull(message = "Password cannot be null")
    @Size(min = 6, message = "Password should have at least 6 characters")
    private String password;
    private RoleType role;
}