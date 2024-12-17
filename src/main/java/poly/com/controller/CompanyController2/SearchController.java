package poly.com.controller.CompanyController2;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import poly.com.Enum.EducationLevel;
import poly.com.Enum.Exp;
import poly.com.Enum.JobLevel;
import poly.com.Enum.WorkType;

import poly.com.dto.request.ProfileDetailsDTO;
import poly.com.dto.response.PageResponse;
import poly.com.dto.response.ProfileSearchResult;

import poly.com.service.FollowService;
import poly.com.service.ProfileService;

import java.util.List;
import java.util.Map;


@Controller
@RequiredArgsConstructor
@RequestMapping("/company")
public class SearchController {

    private final FollowService followService;
    private final ProfileService profileService;

    // Endpoint để tìm kiếm hồ sơ
    @PreAuthorize("hasRole('ROLE_COMPANY')")
    @GetMapping("/search-profiles")
    public String searchProfiles(@RequestParam(defaultValue = "") String name,
                                 @RequestParam(defaultValue = "") String desiredLocation,
                                 @RequestParam(defaultValue = "") WorkType workType,
                                 @RequestParam(defaultValue = "") EducationLevel degree,
                                 @RequestParam(defaultValue = "1") Integer pageNo,
                                 Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Gọi service để thực hiện tìm kiếm hồ sơ
        PageResponse<ProfileSearchResult> response = profileService.searchProfiles(name, desiredLocation, workType, degree, pageNo);
    //    boolean isFollowed = followService.isCompanyFollowingCandidate(companyId, userId);
        for (ProfileSearchResult profile : response.getContent()) {
            boolean isFollowed = followService.isFollowing(profile.getId());
            profile.setFollowed(isFollowed);
        }
        // Thêm danh sách các enum vào model để hiển thị trên view
        model.addAttribute("workTypes", WorkType.values());
        model.addAttribute("jobLevels", JobLevel.values());
        model.addAttribute("experiences", Exp.values());
        model.addAttribute("degrees", EducationLevel.values());

        // Thêm dữ liệu tìm kiếm
        model.addAttribute("profiles", response.getContent());
        model.addAttribute("totalPages", response.getTotalPages());
        model.addAttribute("totalElements", response.getTotalElements());
        model.addAttribute("currentPage", pageNo);

        // Lưu lại giá trị đã chọn
        model.addAttribute("selectedName", name);
        model.addAttribute("selectedDesiredLocation", desiredLocation);
        model.addAttribute("selectedWorkType", workType);
        model.addAttribute("selectedDegree", degree);

        // Trả về view
        return "Company/Timungvienmoi";
    }

    @GetMapping("/details/{profileId}")
    public String getProfileDetails(@PathVariable Long profileId, Model model) {
        // Gọi service để lấy thông tin chi tiết Profile từ 3 bảng
        List<ProfileDetailsDTO> profileDetails = profileService.getProfileDetails(profileId);

        // Thêm dữ liệu vào model
        model.addAttribute("profileDetails", profileDetails);

        // Trả về view (Thymeleaf Template)
        return "Company/profileDetails";
    }


    @GetMapping("/saved-profile")
    public String searchProfiles(@RequestParam(defaultValue = "") String name,
                                 @RequestParam(value = "page", defaultValue = "1") Integer pageNo,
                                 Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Gọi service để lấy kết quả phân trang
        PageResponse<ProfileSearchResult> pageResponse = profileService.searchProfilesSave(name, pageNo);
        for (ProfileSearchResult profile : pageResponse.getContent()) {
            boolean isFollowed = followService.isFollowing(profile.getId());
            profile.setFollowed(isFollowed);
        }
        // Thêm dữ liệu vào model
        model.addAttribute("profiles", pageResponse.getContent());
        model.addAttribute("totalPages", pageResponse.getTotalPages());
        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalElements", pageResponse.getTotalElements());

        // Trả về view, ví dụ 'profiles.html'
        return "Company/hosodaluu"; // Đây là tên file Thymeleaf, ví dụ: profiles.html
    }

    @GetMapping("/search-profiles/{profileId}")
    public String searchProfiles(@PathVariable Long profileId, Model model) {
        // Gọi service để lấy danh sách Profile, Follow, và Company
        List<Map<String, Object>> profiles = profileService.getAllFieldsByProfileId(profileId);

        // Đưa dữ liệu vào model để Thymeleaf sử dụng
        model.addAttribute("profileResults", profiles);

        // Trả về view tương ứng
        return "Company/Timungvienmoi"; // Đây là tên file Thymeleaf
    }


    // Endpoint để theo dõi hoặc bỏ theo dõi một người dùng
    @PreAuthorize("hasRole('ROLE_COMPANY')")
    @PostMapping("/toggle-follow/{userId}")
    @ResponseBody
    public ResponseEntity<?> toggleFollow(@PathVariable Long userId) {
        try {
            followService.toggleFollowUser(userId);
            return ResponseEntity.ok("Cập nhật trạng thái theo dõi thành công.");
        } catch (Exception e) {
            // Log the exception for debugging purposes

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
             .body("Có lỗi xảy ra: " + e.getMessage());
        }
    }


}
