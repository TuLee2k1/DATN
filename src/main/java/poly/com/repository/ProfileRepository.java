package poly.com.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import poly.com.model.Company;
import poly.com.model.JobCategory;
import poly.com.model.Profile;

import java.util.List;
import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
    @Query("select c from Profile c where c.name like concat(?1, '%')")
    List<Profile> findByProfileNameStartsWith(String ProfileName, Pageable pageable);

    @Query("select c from Profile c where c.id = ?1")
    Optional<Profile> findById(Long id);

    List<Profile> findByNameContainsIgnoreCase(String name);

    List<Profile> findByIdNotAndNameContainsIgnoreCase(Long id, String name);
}