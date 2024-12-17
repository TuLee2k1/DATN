package poly.com.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import poly.com.model.Experience;
import poly.com.model.Profile;
import poly.com.model.School;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProfileDetailsDTO {
    private Experience experience;
    private School school;
    private Profile profile;
}
