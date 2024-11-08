package poly.com.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import poly.com.Enum.RoleType;

import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@SuperBuilder
@Table(name= "Roles")
public class Role extends AbstractEntity{


    @Enumerated(EnumType.STRING)
    private RoleType role;  // Kiểu vai trò

    @ManyToMany(mappedBy = "roles", cascade = CascadeType.ALL)
    private List<User2> users;


}
