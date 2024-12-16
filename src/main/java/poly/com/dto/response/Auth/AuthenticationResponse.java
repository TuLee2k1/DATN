package poly.com.dto.response.Auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import poly.com.Enum.RoleType;
import poly.com.model.Profile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("refresh_token")
    private String refreshToken;

    private Long id;
    private String email;
    private String fullName;
    private String firstName;
    private List<RoleType> roles;
    private String redirectUrl;
    private Profile profile;
}
