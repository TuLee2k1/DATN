package poly.com.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import poly.com.Enum.TokenType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "token")
public class Token extends AbstractEntity{

    private String token;

    @Enumerated(EnumType.STRING)
    private TokenType tokenType;

    private boolean expired;

    private boolean revoked;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
