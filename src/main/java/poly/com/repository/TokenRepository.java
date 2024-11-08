package poly.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import poly.com.model.Token;
import poly.com.model.User2;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByToken(String token);
    Optional<Token> findByUser(User2 user);
}
