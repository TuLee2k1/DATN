package poly.com.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.com.exception.CompanyException;
import poly.com.exception.JobPostException;
import poly.com.model.Company;
import poly.com.model.JobPost;
import poly.com.repository.JobPostRepository;

import java.util.List;
import java.util.Optional;

@Service
public class JobPostService {

    @Autowired
    private JobPostRepository jobPostRepository;


    public JobPost save(JobPost entity) {
        return jobPostRepository.save(entity);
    }


    public List<JobPost> findAll() {
        return jobPostRepository.findAll();
    }


    public Page<JobPost> findAll(Pageable pageable) {
        return jobPostRepository.findAll(pageable);
    }

    public  JobPost  update(Long id, JobPost entity) {
        Optional<JobPost> existed = jobPostRepository.findById(id);

        if (existed.isEmpty()) {
            throw new JobPostException("Job Post  "  +  id  + " not found");
        }
        try {
            JobPost  existedJobpost = existed.get();
            existedJobpost.setJobTitle(entity.getJobTitle());
            existedJobpost.setJobDescription(entity.getJobDescription());
            existedJobpost.setQuantity(entity.getQuantity());
            existedJobpost.setJobRequire(entity.getJobRequire());
            existedJobpost.setCreateDate(entity.getCreateDate());
            existedJobpost.setMaxSalary(entity.getMaxSalary());
            existedJobpost.setMinSalary(entity.getMinSalary());
            existedJobpost.setEndDate(entity.getEndDate());
            existedJobpost.setJobCategory_id(entity.getJobCategory_id());
            existedJobpost.setCompany_id(entity.getCompany_id());
            existedJobpost.setStatus(entity.getStatus());

            return jobPostRepository.save(existedJobpost);

        }catch (Exception ex){

            throw new CompanyException("Job Post is update fail");
        }
    }

    public JobPost findById(Long id) {
        Optional<JobPost> found = jobPostRepository.findById(id);

        if (found.isEmpty()) {
            throw new EntityNotFoundException("Company id " + id + " not found");
        }
        return found.get();
    }

    public void deleteById(Long id) {
        JobPost existed = findById(id);

        jobPostRepository.delete(existed);
    }

}
