package poly.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.com.model.Pay;

public interface PayRepository extends JpaRepository<Pay, Long> {
}