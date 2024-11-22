package poly.com.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import poly.com.model.ServicePay;

@Repository
public interface ServicePayRepository extends JpaRepository<ServicePay, Long> {

}
