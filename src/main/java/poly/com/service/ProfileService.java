package poly.com.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import poly.com.Enum.EducationLevel;
import poly.com.Enum.WorkType;
import poly.com.dto.ProfileDTO;
import poly.com.dto.response.PageResponse;
import poly.com.dto.response.ProfileSearchResult;
import poly.com.exception.ProfileException;

import poly.com.model.Company;
import poly.com.model.JobProfile;

import poly.com.model.Profile;
import poly.com.model.User;
import poly.com.repository.ProfileRepository;
import poly.com.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;

    /**
     * Tạo mới hoặc lưu thông tin profile
     */
    public Profile save(@Valid ProfileDTO dto) {
        Profile entity = new Profile();
        BeanUtils.copyProperties(dto, entity);

        // Xử lý upload ảnh logo nếu có
        if (dto.getLogoFile() != null) {
            String fileName = fileStorageService.storeImageProfileFile(dto.getLogoFile());
            entity.setLogo(fileName);
            dto.setLogo(null);
        }

        return profileRepository.save(entity);
    }

    public Profile updateProfile(Long userId, ProfileDTO dto) {
        Optional<Profile> existedOptional = profileRepository.findById(userId);

        if (existedOptional.isEmpty()) {
            throw new ProfileException("Profile không tồn tại.");
        }

        Profile existedProfile = existedOptional.get();

        // Cập nhật thông tin từ DTO
        existedProfile.setName(dto.getName());
        existedProfile.setEmail(dto.getEmail());
        existedProfile.setPhone(dto.getPhone());
        existedProfile.setAddress(dto.getAddress());
        existedProfile.setSex(dto.getSex());
        existedProfile.setDateOfBirth(dto.getDateOfBirth());

        // Xử lý upload ảnh logo nếu có
        MultipartFile logoFile = dto.getLogoFile();
        if (logoFile != null && !logoFile.isEmpty()) {
            String fileName = fileStorageService.storeImageProfileFile(logoFile);
            existedProfile.setLogo(fileName);
        }

        return profileRepository.save(existedProfile);
    }

    /**
     * Cập nhật thông tin profile theo ID
     */
    public Profile update(Long id, Profile entity) {
        Optional<Profile> existed = profileRepository.findById(id);

        if (existed.isEmpty()) {
            throw new ProfileException("Profile " + id + " không tồn tại.");
        }

        try {
            Profile existedProfile = existed.get();
            existedProfile.setName(entity.getName());
            existedProfile.setAddress(entity.getAddress());
            existedProfile.setPhone(entity.getPhone());
            existedProfile.setEmail(entity.getEmail());
            existedProfile.setSex(entity.getSex());
            existedProfile.setDateOfBirth(entity.getDateOfBirth());
            existedProfile.setLogo(entity.getLogo());

            return profileRepository.save(existedProfile);
        } catch (Exception ex) {
            throw new ProfileException("Cập nhật profile thất bại.");
        }
    }

    /**
     * Lấy danh sách tất cả các profile
     */
    public List<Profile> findAll() {
        return profileRepository.findAll();
    }

    /**
     * Lấy danh sách profile phân trang
     */
    public Page<Profile> findAll(org.springframework.data.domain.Pageable pageable) {
        return profileRepository.findAll(pageable);
    }

    /**
     * Tìm profile theo ID
     */
    public Profile findById(Long id) {
        Optional<Profile> found = profileRepository.findById(id);

        if (found.isEmpty()) {
            throw new EntityNotFoundException("Profile ID " + id + " không tồn tại.");
        }
        return found.get();
    }

    /**
     * Xóa profile theo ID
     */
    public void deleteById(Long id) {
        Profile existed = findById(id);
        profileRepository.delete(existed);
    }


    /**
     * Thay đổi mật khẩu người dùng
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng."));

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu hiện tại không đúng.");
        }

        // Cập nhật mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public PageResponse<ProfileSearchResult> searchProfiles(String name, String desiredLocation, WorkType workType,
                                                            EducationLevel degree, Integer pageNo) {
        // Tạo đối tượng pageable, mỗi trang sẽ có 10 kết quả
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        // Gọi repository để lấy kết quả phân trang
        Page<ProfileSearchResult> page = profileRepository.searchProfilesWithAgeAndCount(name, desiredLocation, workType, degree, pageable);

        // Trả về PageResponse
        return new PageResponse<>(page);
    }
}
