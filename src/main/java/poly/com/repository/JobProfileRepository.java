package poly.com.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import poly.com.model.JobCategory;
import poly.com.model.JobProfile;
import poly.com.model.Profile;

import java.util.List;

public interface JobProfileRepository extends JpaRepository<JobProfile, Long> {

}