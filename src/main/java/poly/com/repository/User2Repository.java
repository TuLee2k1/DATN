package poly.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.com.model.User2;

import java.util.Optional;

@Repository
public interface User2Repository extends JpaRepository<User2, Long> {
    Optional<User2> findByEmail(String email);
    boolean existsByEmail(String email);
}
