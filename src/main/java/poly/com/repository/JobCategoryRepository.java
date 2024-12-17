package poly.com.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import poly.com.model.JobCategory;

import java.util.List;
import java.util.Optional;

public interface JobCategoryRepository extends JpaRepository<JobCategory, Long> {

  List<JobCategory> findByCategoryNameStartsWith(String categoryName, Pageable pageable);

  Page<JobCategory> findAll(Pageable pageable);

  Optional<JobCategory> findByCategoryName(String categoryName);
}