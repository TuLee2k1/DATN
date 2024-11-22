package poly.com.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserRoleDTO {
    private Long id;
    private boolean role;
    private Long userId; // ID của User mà bạn muốn liên kết
}
