package poly.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import poly.com.model.Experience;

import java.util.List;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    List<Experience> findByProfileId(Long profileId);
}
