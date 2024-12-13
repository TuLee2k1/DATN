package poly.com.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.com.dto.request.ResumeRequest;
import poly.com.model.Experience;
import poly.com.model.Profile;
import poly.com.model.School;
import poly.com.repository.ExperienceRepository;
import poly.com.repository.ProfileRepository;
import poly.com.repository.SchoolRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class resumeService {
    private final ProfileRepository profileRepository;
    private final SchoolRepository schoolRepository;
    private final ExperienceRepository experienceRepository;

    @Transactional
    public Profile saveOrUpdateResume(ResumeRequest request, Long userId) {
        // Tìm hoặc tạo profile
        Profile profile = profileRepository.findById(userId)
                .orElseGet(() -> {
                    Profile newProfile = new Profile();
                    newProfile.setId(userId);
                    return profileRepository.save(newProfile);
                });

        // Cập nhật thông tin profile
        profile.setName(request.getName());
        profile.setEmail(request.getEmail());
        profile.setPhone(request.getPhone());
        profile.setAddress(request.getAddress());
        profile.setSex(request.getSex());
        profile.setDateOfBirth(request.getDateOfBirth());

        Profile savedProfile = profileRepository.save(profile);

        // Xử lý School
        Optional<School> existingSchool = schoolRepository.findAll().stream()
                .filter(s -> s.getProfile_id().getId().equals(userId))
                .findFirst();

        School school = existingSchool.orElseGet(() -> {
            School newSchool = new School();
            newSchool.setProfile_id(savedProfile);
            return newSchool;
        });

        school.setSchoolName(request.getSchoolName());
        school.setDegree(request.getDegree());
        school.setStartDate(request.getStartDate());
        school.setEndDate(request.getEndDate());
        school.setGPA(request.getGPA());

        schoolRepository.save(school);

        // Xử lý Experience
        Optional<Experience> existingExperience = experienceRepository.findByProfileId(userId)
                .stream().findFirst();

        Experience experience = existingExperience.orElseGet(() -> {
            Experience newExperience = new Experience();
            newExperience.setProfile(savedProfile);
            return newExperience;
        });

        experience.setJobTitle(request.getJobTitle());
        experience.setCompanyName(request.getCompanyName());
        experience.setJobDescription(request.getJobDescription());
        experience.setStartDate(request.getStart());
        experience.setEndDate(request.getEnd());

        experienceRepository.save(experience);

        return savedProfile;
    }

    @Transactional(readOnly = true)
    public ResumeRequest getResumeByUserId(Long userId) {
        Profile profile = profileRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        ResumeRequest resumeRequest = new ResumeRequest();

        // Điền thông tin Profile
        resumeRequest.setName(profile.getName());
        resumeRequest.setEmail(profile.getEmail());
        resumeRequest.setPhone(profile.getPhone());
        resumeRequest.setAddress(profile.getAddress());
        resumeRequest.setSex(profile.getSex());
        resumeRequest.setDateOfBirth(profile.getDateOfBirth());

        // Tìm và điền thông tin School
        schoolRepository.findAll().stream()
                .filter(s -> s.getProfile_id().getId().equals(userId))
                .findFirst()
                .ifPresent(school -> {
                    resumeRequest.setSchoolName(school.getSchoolName());
                    resumeRequest.setDegree(school.getDegree());
                    resumeRequest.setStartDate(school.getStartDate());
                    resumeRequest.setEndDate(school.getEndDate());
                    resumeRequest.setGPA(school.getGPA());
                });

        // Tìm và điền thông tin Experience
        experienceRepository.findByProfileId(userId)
                .stream().findFirst()
                .ifPresent(experience -> {
                    resumeRequest.setJobTitle(experience.getJobTitle());
                    resumeRequest.setCompanyName(experience.getCompanyName());
                    resumeRequest.setJobDescription(experience.getJobDescription());
                    resumeRequest.setStart(experience.getStartDate());
                    resumeRequest.setEnd(experience.getEndDate());
                });

        return resumeRequest;
    }
}