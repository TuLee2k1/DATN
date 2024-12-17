package poly.com.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.com.model.SubCategory;

import java.util.List;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory,Long> {

    List<SubCategory> findByJobCategoryId(Long jobCategoryId);


    boolean existsByJobCategoryId(Long jobCategoryId);

    Page<SubCategory> findAll( Pageable pageable);

}
