package poly.com.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import poly.com.model.Company;
import poly.com.model.JobPost;

import java.util.List;
import java.util.Optional;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {

  @Query("select c from JobPost c where c.jobTitle like concat(?1, '%')")
  List<JobPost> findByJobTitleStartsWith(String jobTitle, Pageable pageable);

  @Query("select c from JobPost c where c.id = ?1")
  Optional<JobPost> findById(Long id);
}