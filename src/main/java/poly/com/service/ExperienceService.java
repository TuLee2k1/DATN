package poly.com.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.com.exception.ExperienceException;
import poly.com.model.Experience;
import poly.com.repository.ExperienceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ExperienceService {
    @Autowired
    private ExperienceRepository experienceRepository;

    public Experience save(Experience entity) {
        return experienceRepository.save(entity);
    }

    public Experience update(Long id, Experience entity) {
        Optional<Experience> existed = experienceRepository.findById(id);

        if (existed.isEmpty()) {
            throw new ExperienceException("Experience " + id + " not found");
        }

        Experience existedExperience = existed.get();
        existedExperience.setJobTitle(entity.getJobTitle());
        existedExperience.setJobDescription(entity.getJobDescription());
        existedExperience.setCompanyName(entity.getCompanyName());
        existedExperience.setStartDate(entity.getStartDate());
        existedExperience.setEndDate(entity.getEndDate());

        return experienceRepository.save(existedExperience);
    }

    public List<Experience> findAll() {
        return experienceRepository.findAll();
    }

    public Experience findById(Long id) {
        Optional<Experience> found = experienceRepository.findById(id);

        if (found.isEmpty()) {
            throw new EntityNotFoundException("Experience id " + id + " not found");
        }
        return found.get();
    }

    public void deleteById(Long id) {
        Experience existed = findById(id);
        experienceRepository.delete(existed);
    }
}
