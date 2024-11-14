package poly.com.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import poly.com.model.Company;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("select c from Company c where c.name like concat(?1, '%')")
    List<Company> findByNameStartsWith(String name, Pageable pageable);

    @Query("select c from Company c where c.id = ?1")
    Optional<Company> findById(Long id);

    List<Company> findByNameContainsIgnoreCase(String name);

    List<Company> findByIdNotAndNameContainsIgnoreCase(Long id, String name);

}