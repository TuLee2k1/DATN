package poly.com.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.com.dto.CompanyDto;
import poly.com.dto.ProfileDTO;
import poly.com.exception.CompanyException;
import poly.com.exception.ProfileException;
import poly.com.model.Company;
import poly.com.model.Profile;
import poly.com.repository.ProfileRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public  Profile  save(@Valid ProfileDTO dto) {

//        List<?> foudedList = profileRepository.findByNameContainsIgnoreCase(dto.getName());
//
//        if (foudedList.size()>0) {
//            throw new CompanyException("Company name already exist");
//        }

        Profile entity = new Profile();

        BeanUtils.copyProperties(dto, entity);

        if (dto.getLogoFile() != null){
            String fileName = fileStorageService.storeImageProfileFile(dto.getLogoFile());
            entity.setLogo(fileName);
            dto.setLogo(null);
        }
        return profileRepository.save(entity);
    }

    public  Profile  update(Long id, Profile entity) {
        Optional<Profile> existed = profileRepository.findById(id);

        if (existed.isEmpty()) {
            throw new ProfileException("Profile "  +  id  + " not found");
        }
        try {
            Profile  existedProfile = existed.get();
            existedProfile.setName(entity.getName());
            existedProfile.setAddress(entity.getAddress());
            existedProfile.setPhone(entity.getPhone());
            existedProfile.setEmail(entity.getEmail());
            existedProfile.setSex(entity.getSex());
            existedProfile.setDateOfBirth(entity.getDateOfBirth());
            existedProfile.setLogo(entity.getLogo());


            return profileRepository.save(existedProfile);

        }catch (Exception ex){

            throw new CompanyException("Profile is update fail");
        }
    }

    public List<Profile> findAll() {
        return profileRepository.findAll();
    }

    public Page<Profile> findAll(Pageable pageable) {
        return profileRepository.findAll(pageable);
    }

    public Profile findById(Long id) {
        Optional<Profile> found = profileRepository.findById(id);

        if (found.isEmpty()) {
            throw new EntityNotFoundException("Profile id " + id + " not found");
        }
        return found.get();
    }

    public void deleteById(Long id) {
        Profile existed =findById(id);

        profileRepository.delete(existed);
    }
}
