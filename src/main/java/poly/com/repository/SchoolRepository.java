package poly.com.repository;

import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.com.model.School;

@Repository
public interface SchoolRepository extends JpaRepository<School, Long> {

}
