package poly.com.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import poly.com.Enum.RoleType;

import java.util.List;

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


    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private List<User> users;


}
