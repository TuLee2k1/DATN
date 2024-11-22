package poly.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.com.model.JobProfile;
import poly.com.repository.JobProfileRepository;

import java.util.List;
import java.util.Optional;

@Service
public class JobProfileService {

    @Autowired
    private JobProfileRepository jobProfileRepository;

    public Page<JobProfile> getAllJobProfiles(Pageable pageable) {
        return jobProfileRepository.findAll(pageable);
    }

    public List<JobProfile> getAllJobProfiles() {
        return jobProfileRepository.findAll();
    }

    public Optional<JobProfile> getJobProfileById(Long id) {
        return jobProfileRepository.findById(id);
    }

    public JobProfile saveJobProfile(JobProfile jobProfile) {
        return jobProfileRepository.save(jobProfile);
    }

    public void deleteJobProfile(Long id) {
        jobProfileRepository.deleteById(id);
    }
}

