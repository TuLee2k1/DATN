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
import poly.com.Util.AuthenticationUtil;
import poly.com.dto.ProfileDTO;
import poly.com.dto.request.profileRequest;
import poly.com.dto.response.PageResponse;
import poly.com.dto.response.ProfileSearchResult;
import poly.com.exception.ProfileException;

import poly.com.model.*;

import poly.com.repository.FollowRepository;
import poly.com.repository.ProfileRepository;
import poly.com.repository.UserRepository;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ProfileService {

    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationUtil authenticationUtil;
    private final FollowRepository followRepository;

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

    public Profile saveProfile(profileRequest request, Long userId) {
        Optional<Profile> existingProfile = profileRepository.findById(userId);
        Profile profile;

        if (existingProfile.isPresent()) {
            profile = existingProfile.get();
        } else {
            profile = new Profile();
        }

        // Cập nhật thông tin từ request
        profile.setName(request.getName());
        profile.setEmail(request.getEmail());
        profile.setPhone(request.getPhone());
        profile.setAddress(request.getAddress());
        profile.setSex(request.getSex());
        profile.setDateOfBirth(request.getDateOfBirth());

        // Xử lý file logo nếu có
        if (request.getLogoFile() != null && !request.getLogoFile().isEmpty()) {
            String fileName = fileStorageService.storeImageProfileFile(request.getLogoFile());
            profile.setLogo(fileName);
        }

        return profileRepository.save(profile);
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

    public Page<Profile> getAllByAdmin(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 5);
        return profileRepository.findAll(pageable);
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




    /**
     * Tìm kiếm các hồ sơ với các tham số tìm kiếm và phân trang.
     * @param name Tên người dùng
     * @param desiredLocation Vị trí mong muốn
     * @param workType Loại công việc
     * @param degree Bằng cấp

     * @return Trang kết quả tìm kiếm
     */
//    public PageResponse<ProfileSearchResult> searchProfiles(String name, String desiredLocation,
//                                                            WorkType workType, String degree, Integer pageNo) {
//        Pageable pageable = PageRequest.of(pageNo - 1, 10);
//
//        Page<ProfileSearchResult> results = profileRepository.searchProfilesWithAgeAndCount(name, desiredLocation, workType, degree, pageable);
//
//        return new PageResponse<>(results);
//    }


    public PageResponse<ProfileSearchResult> searchProfiles(String name, String desiredLocation, WorkType workType,
                                                            EducationLevel degree, Integer pageNo) {

        // Tạo đối tượng pageable, mỗi trang sẽ có 10 kết quả
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        // Gọi repository để lấy kết quả phân trang
        Page<ProfileSearchResult> page = profileRepository.searchProfilesWithAgeAndCount(name, desiredLocation, workType, degree, pageable);
        return new PageResponse<>(page);
    }

    public PageResponse<ProfileSearchResult> searchProfilesSave(String name, Integer pageNo) {
        var companyID = authenticationUtil.getCurrentUser().getCompany().getId();
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        Page<ProfileSearchResult> page = profileRepository.searchProfilesWithCompanyIdAndName(companyID,name, pageable);
        return new PageResponse<>(page);
    }

    public List<Map<String, Object>> getAllFieldsByProfileId(Long userId) {
        List<Object[]> results = profileRepository.searchProfileWithAllFields(userId);

        List<Map<String, Object>> response = new ArrayList<>();
        for (Object[] result : results) {
            Profile profile = (Profile) result[0];
            Follow follow = (Follow) result[1];
            Company company = (Company) result[2];

            Map<String, Object> map = new HashMap<>();
            map.put("profile", profile);
            map.put("follow", follow);
            map.put("company", company);

            response.add(map);
        }
        return response;
    }

}
