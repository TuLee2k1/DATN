package poly.com.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import poly.com.Enum.StatusEnum;
import poly.com.dto.request.JobPost.JobPostTitleResponse;
import poly.com.dto.response.JobPost.JobListingResponse;
import poly.com.model.JobPost;

import java.util.List;
import java.util.Optional;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {

  @Query("select c from JobPost c where c.jobTitle like concat(?1, '%')")
  List<JobPost> findByJobTitleStartsWith(String jobTitle, Pageable pageable);

  @Query("select c from JobPost c where c.id = ?1")
  Optional<JobPost> findById(Long id);

  @Query("SELECT COUNT(j) FROM JobPost j")
  Long countAllJobPosts();
  Page<JobPost> findAllByJobTitleContainingAndStatusEnum(String jobTitle, StatusEnum statusEnum, Pageable pageable);

  @Query("SELECT new poly.com.dto.request.JobPost.JobPostTitleResponse(c.id, c.jobTitle) FROM JobPost c")
  List<JobPostTitleResponse> getJobPostTitle();

  @Query("SELECT new poly.com.dto.response.JobPost.JobListingResponse(c.id,c.jobTitle,c.createDate,c.endDate,c" +
          ".appliedCount,c.status,c.statusEnum) " +
          "FROM JobPost c")
  List<JobListingResponse> getJobListings();
}