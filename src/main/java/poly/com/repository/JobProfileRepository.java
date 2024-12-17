package poly.com.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.com.Enum.StatusEnum;
import poly.com.model.*;

import java.util.List;

public interface JobProfileRepository extends JpaRepository<JobProfile, Long> {

    @Query("SELECT COUNT(jp) FROM JobProfile jp")
    Long countAllJobProfiles();

    @Query("SELECT COUNT(jp.status) FROM JobProfile jp WHERE jp.status = :status")
    public Long countByStatus(StatusEnum status);

    @Query("SELECT jp FROM JobProfile jp WHERE jp.jobPost.id = :jobPostId")
    Page<JobProfile> findJobProfilesByJobPostId(@Param("jobPostId") Long jobPostId, Pageable pageable);

    // Đếm số lượng ứng viên cho một bài đăng
    @Query("SELECT COUNT(jp) FROM JobProfile jp WHERE jp.jobPost.id = :jobPostId")
    Long countByJobPostId(@Param("jobPostId") Long jobPostId);

    List<JobProfile> findByJobPost_IdAndJobPost_Company(Long jobPostId, Company company);

    @Query("SELECT COUNT(jp) FROM JobProfile jp WHERE  jp.jobPost.company = :company")
 Long countByCompany( @Param("company") Company company);

    Page<JobProfile> findByJobPost_Company(Company company, Pageable pageable);


    @Query("SELECT jp FROM JobProfile jp WHERE jp.status = :status")
    Page<JobProfile> findByStatus(@Param("status") StatusEnum status, Pageable pageable
    );

    Page<JobProfile> findByJobPostIdAndStatus(Long jobPostId, StatusEnum status, Pageable pageable);

    // Lọc theo jobPostId và status
    List<JobProfile> findByJobPost_IdAndStatus(Long jobPostId, StatusEnum status);

    // Lọc theo jobPostId
    List<JobProfile> findByJobPost_Id(Long jobPostId);

    // Lọc theo status
    List<JobProfile> findByStatus(StatusEnum status);

    // Lọc theo công ty
    List<JobProfile> findByJobPost_Company(Company company);

    List<JobProfile> findByUser_Id(Long userId);


    @Query("SELECT COUNT(jp) FROM JobProfile jp WHERE jp.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(jp.status) FROM JobProfile jp WHERE jp.status = :status AND jp.jobPost.company = :company")
    public Long countByStatus(StatusEnum status, Company company);

    @Query("SELECT COUNT(jp.status) FROM JobProfile jp WHERE jp.jobPost.id = :jobPostId AND jp.status = :status")
    Long countByJobPostIdAndStatus(
     @Param("jobPostId") Long jobPostId,
     @Param("status") StatusEnum status
    );

    @Query("SELECT COUNT(jp) FROM JobProfile jp WHERE jp.jobPost.id = :jobPostId AND jp.jobPost.company = :company")
    Long countByJobPostIdAndCompany(@Param("jobPostId") Long jobPostId, @Param("company") Company company);

}