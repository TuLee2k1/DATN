package poly.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import poly.com.model.VerifyUser;
import poly.com.model.User;

public interface VerifyRepository extends JpaRepository<VerifyUser, Long> {
    Optional<VerifyUser> findByToken(String token);
    Optional<VerifyUser> findByUser(User user);
}
