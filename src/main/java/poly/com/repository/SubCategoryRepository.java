package poly.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.com.model.SubCategory;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory,Long> {
    List<SubCategory> findByJobCategoryId(Long jobCategoryId);
}
