package poly.com.controller.CompanyController2;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import poly.com.Enum.EducationLevel;
import poly.com.Enum.Exp;
import poly.com.Enum.JobLevel;
import poly.com.Enum.WorkType;
import poly.com.dto.response.PageResponse;
import poly.com.dto.response.ProfileSearchResult;
import poly.com.model.Follow;
import poly.com.service.FollowService;
import poly.com.service.ProfileService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/search-profiles")
public class SearchController {

    private final FollowService followService;
    private final ProfileService profileService;

    // Endpoint để tìm kiếm hồ sơ
    @GetMapping
    public String searchProfiles(@RequestParam(required = false) String name,
                                 @RequestParam(required = false) String desiredLocation,
                                 @RequestParam(required = false) WorkType workType,
                                 @RequestParam(required = false) EducationLevel degree,
                                 @RequestParam(defaultValue = "1") Integer pageNo,
                                 Model model) {
        // Gọi service để thực hiện tìm kiếm hồ sơ
        PageResponse<ProfileSearchResult> response = profileService.searchProfiles(name, desiredLocation, workType, degree, pageNo);


        // Thêm danh sách các enum vào model để hiển thị trên view
        model.addAttribute("workTypes", WorkType.values());
        model.addAttribute("jobLevels", JobLevel.values());
        model.addAttribute("experiences", Exp.values());
        model.addAttribute("degrees", EducationLevel.values());

        // Thêm dữ liệu tìm kiếm
        model.addAttribute("profiles", response.getContent());
        model.addAttribute("totalPages", response.getTotalPages());
        model.addAttribute("totalElements", response.getTotalElements());

        // Lưu lại giá trị đã chọn
        model.addAttribute("selectedName", name);
        model.addAttribute("selectedDesiredLocation", desiredLocation);
        model.addAttribute("selectedWorkType", workType);
        model.addAttribute("selectedDegree", degree);

        // Trả về view
        return "Company/Timungvienmoi";
    }

    @PostMapping("/toggleFollow/{userId}")
    public String toggleFollowApplicant(@PathVariable("userId") Long userId, Model model) {
        // Gọi service để theo dõi hoặc bỏ theo dõi ứng viên
        Follow follow = followService.toggleFollowApplicant(userId);
        return "Company/Timungvienmoi"; // Trả về view với dữ liệu đã cập nhật
    }



}
