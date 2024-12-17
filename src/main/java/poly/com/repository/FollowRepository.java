package poly.com.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import poly.com.dto.response.Follow.CompanyFollowResponse;
import poly.com.dto.response.Follow.CompanyTrackedCandidateResponse;
import poly.com.dto.response.Follow.JobPostFollowResponse;
import poly.com.dto.response.Follow.UserExperienceResponse;
import poly.com.model.Follow;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {


    List<Follow> findByUserId(Long userId);

    List<Follow> findByCompanyId(Long companyId);

    Optional<Follow> findByUserIdAndCompanyIdAndJobPostId(Long userId, Long companyId, Long jobPostId);

    Optional<Follow> findByUserIdAndJobPostId(Long userId, Long jobPostId);

    Optional<Follow> findByCompanyIdAndUserId(Long companyId, Long userId);

    Page<Follow> findByUserId(Long userId, Pageable pageable);
    void deleteByJobPostIdAndUserId(Long jobPostId, Long userId);

    Optional<Follow> findByUserIdAndCompanyId (Long userId, Long companyId);

    @Query("""
        SELECT new poly.com.dto.response.Follow.JobPostFollowResponse(
            jp.id,
            c.logo,
            jp.jobTitle,
            c.name,
            jp.minSalary,
            jp.maxSalary,
            jp.city
        )
        FROM Follow f
        JOIN JobPost jp ON f.jobPostId = jp.id
        JOIN Company c ON jp.company.id = c.id
        WHERE f.userId = :userId
    """)
    Page<JobPostFollowResponse> findUserFollowedJobPosts(@Param("userId") Long userId, Pageable pageable);


    @Query(value = """  
    SELECT
        c.logo AS companyLogo,
        c.name AS companyName,
        CONCAT(c.address, ', ', c.district, ', ', c.city) AS address,
        c.employee_count AS employeeCount,
        (SELECT COUNT(*)FROM job_post jp WHERE jp.company_id = c.id) AS jobPostCount
    FROM follows f
    JOIN company c ON f.company_id = c.id
    WHERE f.user_id = :userId
    ORDER BY c.name ASC """, nativeQuery = true)
    Page<CompanyFollowResponse> findCompaniesFollowedByUser(@Param("userId") Long userId,Pageable pageable);

    boolean existsByCompanyIdAndUserId (Long companyId, Long userid);

    @Query("SELECT COUNT(f) FROM Follow f WHERE f.userId = :userId")
    Long countBookmarksByUserId(@Param("userId") Long userId);


}
