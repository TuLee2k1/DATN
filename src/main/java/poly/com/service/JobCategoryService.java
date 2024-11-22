package poly.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.com.exception.JobCategoryException;
import poly.com.model.JobCategory;
import poly.com.repository.JobCategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class JobCategoryService {
    @Autowired
    private JobCategoryRepository jobCategoryRepository;

    public JobCategory save(JobCategory entity) {
        return jobCategoryRepository.save(entity);
    }

    public JobCategory update(Long id, JobCategory entity) {
        Optional<JobCategory> existed = jobCategoryRepository.findById(id);

        if (existed.isEmpty()) {
            throw new JobCategoryException("Job Category id " + id + " not found!");
        }
        try
        {
            JobCategory existedJobCategory = existed.get();
            existedJobCategory.setCategoryName(entity.getCategoryName());

            return  jobCategoryRepository.save(existedJobCategory);

        }catch (Exception ex ){
            throw new JobCategoryException("Job Category is update fail!");
        }

    }

    public List<JobCategory> findAll() {
        return jobCategoryRepository.findAll();
    }

    public JobCategory findById(Long id) {
        Optional<JobCategory> found = jobCategoryRepository.findById(id);
        if (found.isEmpty()) {
            throw new JobCategoryException("Job Category id " + id + " not found!");
        }
        return found.get();
    }

    public void delete(Long id) {
        JobCategory existed = findById(id);

        jobCategoryRepository.delete(existed);
    }
}

