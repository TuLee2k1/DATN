package poly.com.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.com.Enum.StatusEnum;
import poly.com.dto.request.JobPost.JobPostTitleResponse;
import poly.com.dto.response.JobPost.JobListActiveResponse;
import poly.com.dto.response.JobPost.JobListingResponse;
import poly.com.model.Company;
import poly.com.model.JobPost;

import java.util.List;
import java.util.Optional;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {


  List<JobPost> findByStatusEnum(StatusEnum statusEnum); // Lọc bài đăng theo trạng thái cho admin


  @Query("select c from JobPost c where c.jobTitle like concat(?1, '%')")
  List<JobPost> findByJobTitleStartsWith(String jobTitle, Pageable pageable);

  @Query("select c from JobPost c where c.id = ?1")
  Optional<JobPost> findById(Long id);

  @Query("SELECT new poly.com.dto.response.JobPost.JobListingResponse(" +
          "c.id, c.jobTitle, c.createDate, c.endDate, c.appliedCount, c.status, c.statusEnum) " +
          "FROM JobPost c " +
          "WHERE c.company = :company " +
          "AND (:jobTitle IS NULL OR c.jobTitle LIKE %:jobTitle%) " +
          "AND (:statusEnum IS NULL OR c.statusEnum = :statusEnum)")
  Page<JobListingResponse> findAllByJobTitleContainingAndStatusEnum(
          @Param("jobTitle") String jobTitle,
          @Param("statusEnum") StatusEnum statusEnum,
          Pageable pageable,
          @Param("company") Company company
  );
  @Query("SELECT new poly.com.dto.response.JobPost.JobListingResponse(" +
          "c.id, c.jobTitle, c.createDate, c.endDate, c.appliedCount, c.status, c.statusEnum) " +
          "FROM JobPost c")
  Page<JobPost> findAll(Pageable pageable);

  @Query("SELECT new poly.com.dto.request.JobPost.JobPostTitleResponse(c.id, c.jobTitle) FROM JobPost c WHERE c.company = :company")
  List<JobPostTitleResponse>  getJobPostTitleByCompany(@Param("company") Company company);

  @Query("SELECT COUNT(j) FROM JobPost j")
  Long countAllJobPosts();
  Page<JobPost> findAllByJobTitleContainingAndStatusEnum(String jobTitle, StatusEnum statusEnum, Pageable pageable);

  @Query("SELECT new poly.com.dto.request.JobPost.JobPostTitleResponse(c.id, c.jobTitle) FROM JobPost c")
  List<JobPostTitleResponse> getJobPostTitle();


  @Query("SELECT new poly.com.dto.response.JobPost.JobListingResponse(c.id,c.jobTitle,c.createDate,c.endDate,c" +
          ".appliedCount,c.status,c.statusEnum) " +
          "FROM JobPost c")
  List<JobListingResponse> getJobListings();




  @Query("SELECT jp FROM JobPost jp WHERE (:statusEnum IS NULL OR jp.statusEnum = :statusEnum) " +
          "AND (:jobTitle IS NULL OR jp.jobTitle LIKE %:jobTitle%)")
  Page<JobPost> findByAdmin(Pageable pageable, @Param("statusEnum") StatusEnum statusEnum, @Param("jobTitle") String jobTitle);



  // Phương thức tìm kiếm theo trạng thái
  Page<JobPost> findByStatusEnum(StatusEnum statusEnum, Pageable pageable);

  List<JobPost> findByStatusEnum(StatusEnum statusEnum);



  @Modifying
  @Transactional
  @Query("DELETE FROM JobPost WHERE id = ?1 AND statusEnum IN (poly.com.Enum.StatusEnum.PENDING, poly.com.Enum.StatusEnum.REJECTED)")
  void deleteJobPostByStatusEnum(Long id);

  // Kiểm tra xem có bài đăng nào sử dụng subCategoryId không
  boolean existsBySubCategoryId(Long subCategoryId);

  // Kiểm tra xem có bài đăng nào sử dụng jobCategoryId không
  boolean existsByJobCategoryId(Long jobCategoryId);



}