package poly.com.controller;

import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import poly.com.dto.ExperienceDTO;
import poly.com.model.Experience;
import poly.com.model.Profile;
import poly.com.service.ExperienceService;
import poly.com.service.MapValidationErrorService;
import poly.com.service.ProfileService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/experience")
public class ExperienceController {

    @Autowired
    private ExperienceService experienceService;

    @Autowired
    private ProfileService profileService;

    @Autowired
    private MapValidationErrorService mapValidationErrorService;

    @PostMapping
    public ResponseEntity<?> saveOrUpdate(@Valid @RequestBody ExperienceDTO dto, BindingResult result) {
        ResponseEntity<?> responseEntity = mapValidationErrorService.mapValidationFields(result);

        if (responseEntity != null) {
            return responseEntity;
        }

        Experience entity = new Experience();
        BeanUtils.copyProperties(dto, entity);


        Profile profile = profileService.findById(dto.getProfileId());
        entity.setProfile(profile); // Thiết lập profile

        entity = experienceService.save(entity);
        dto.setId(entity.getId());
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateExperience(@PathVariable("id") Long id, @RequestBody ExperienceDTO dto) {
        Experience entity = new Experience();
        BeanUtils.copyProperties(dto, entity);


        Profile profile = profileService.findById(dto.getProfileId());
        entity.setProfile(profile); // Thiết lập profile

        entity = experienceService.update(id, entity);
        dto.setId(entity.getId());

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<Experience>> getAllExperiences() {
        List<Experience> experiences = experienceService.findAll();
        return new ResponseEntity<>(experiences, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getExperienceById(@PathVariable("id") Long id) {
        Experience experience = experienceService.findById(id);
        return new ResponseEntity<>(experience, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExperience(@PathVariable("id") Long id) {
        experienceService.deleteById(id);
        return new ResponseEntity<>("Experience with Id " + id + " was deleted", HttpStatus.OK);
    }
}
