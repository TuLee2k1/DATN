package poly.com.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.com.Enum.EducationLevel;
import poly.com.Enum.WorkType;
import poly.com.dto.response.ProfileSearchResult;
import poly.com.model.Company;
import poly.com.model.JobCategory;
import poly.com.model.JobProfile;
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

    @Query("SELECT new poly.com.dto.response.ProfileSearchResult(" +
        "p.user_id.id, " +
     "p.name, " +
     "YEAR(CURRENT_DATE) - YEAR(p.dateOfBirth), " +  // Tính tuổi
     "p.desiredLocation, " +
     "p.desired_salary, " +
     "p.address, " +
     "COUNT(p) OVER(), " +  // Đếm tất cả kết quả (tổng số)
     "p.workType) " +
     "FROM Profile p " +
     "LEFT JOIN School s ON p.id = s.profile_id.id " +
     "LEFT JOIN Experience e ON p.id = e.profile.id " +
     "WHERE (:name IS NULL OR p.name LIKE %:name%) " +
     "AND (:desiredLocation IS NULL OR p.desiredLocation LIKE %:desiredLocation%) " +
     "AND (:workType IS NULL OR p.workType = :workType) " +
     "AND (:degree IS NULL OR s.degree = :degree)")
    Page<ProfileSearchResult> searchProfilesWithAgeAndCount(@Param("name") String name,
                                                            @Param("desiredLocation") String desiredLocation,
                                                            @Param("workType") WorkType workType,
                                                            @Param("degree") EducationLevel degree,
                                                            Pageable pageable);

    Page<Profile> findAll(Pageable pageable);

}