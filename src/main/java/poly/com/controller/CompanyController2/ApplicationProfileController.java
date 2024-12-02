package poly.com.controller.CompanyController2;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import poly.com.Enum.StatusEnum;
import poly.com.dto.request.JobPost.JobPostTitleResponse;
import poly.com.dto.response.Auth.AuthenticationResponse;
import poly.com.dto.response.PageResponse;
import poly.com.model.JobPost;
import poly.com.model.JobProfile;
import poly.com.model.User;
import poly.com.service.JobPostService;
import poly.com.service.JobProfileService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/Company/ApplicationProfile")
@Slf4j
@SessionAttributes("user")
public class ApplicationProfileController {

    private final JobProfileService jobProfileService;
    private final JobPostService jobPostService;

    @PreAuthorize("hasRole('ROLE_COMPANY')")
    @GetMapping
    public String getApplicationProfile(
     Model model,
     Authentication authentication,
     @RequestParam(defaultValue = "") Long jobPostId,
     @RequestParam(defaultValue = "1") Integer pageNo,
     @RequestParam(required = false) StatusEnum status // Thêm tham số trạng thái vào URL
    ) {

        User user = (User) authentication.getPrincipal();

        // Lấy danh sách các bài đăng công việc
        List<JobPostTitleResponse> companyJobPosts = jobPostService.getJobPostTitle();

        // Đếm hồ sơ theo trạng thái
        Long totalProfiles = jobProfileService.countTotalProfilesByJobPost(jobPostId);
        Long pendingProfiles = jobProfileService.countProfilesByJobPostAndStatus(jobPostId, StatusEnum.PENDING);
        Long verifiedProfiles = jobProfileService.countProfilesByJobPostAndStatus(jobPostId, StatusEnum.VERIFIED);
        Long rejectedProfiles = jobProfileService.countProfilesByJobPostAndStatus(jobPostId, StatusEnum.REJECTED);
        Long activeProfiles = jobProfileService.countProfilesByJobPostAndStatus(jobPostId, StatusEnum.ACTIVE);
        Long inactiveProfiles = jobProfileService.countProfilesByJobPostAndStatus(jobPostId, StatusEnum.INACTIVE);
        Long deletedProfiles = jobProfileService.countProfilesByJobPostAndStatus(jobPostId, StatusEnum.DELETED);

        log.info("Total Profiles: {}", totalProfiles);
        log.info("Pending Profiles: {}", pendingProfiles);
        log.info("Verified Profiles: {}", verifiedProfiles);
        log.info("Rejected Profiles: {}", rejectedProfiles);
        log.info("Active Profiles: {}", activeProfiles);
        log.info("Inactive Profiles: {}", inactiveProfiles);
        log.info("Deleted Profiles: {}", deletedProfiles);

        // Fetch job profiles based on jobPostId and status
        PageResponse<JobProfile> jobProfiles;
        if (jobPostId != null && status != null) {
            // Nếu có jobPostId và status, lọc theo jobPostId và status
            jobProfiles = jobProfileService.getAllProfilesByJobPostAndStatus(jobPostId, status, pageNo);
        } else if (jobPostId != null) {
            // Nếu chỉ có jobPostId, lấy tất cả hồ sơ theo jobPostId
            jobProfiles = jobProfileService.getALlProfileByJobPostid(jobPostId, pageNo);
        } else {
            // Nếu không có jobPostId, lấy tất cả hồ sơ của công ty
            jobProfiles = jobProfileService.getAllProfilesByCompany(user.getCompany(), pageNo);
        }

        model.addAttribute("jobProfiles", jobProfiles.getContent());
        model.addAttribute("jobPostsTitle", companyJobPosts);
        model.addAttribute("totalPages", jobProfiles.getTotalPages());
        model.addAttribute("currentPage", pageNo);

        // Thêm số lượng hồ sơ theo trạng thái vào model
        model.addAttribute("totalProfiles", totalProfiles);
        model.addAttribute("pendingProfiles", pendingProfiles);
        model.addAttribute("verifiedProfiles", verifiedProfiles);
        model.addAttribute("rejectedProfiles", rejectedProfiles);
        model.addAttribute("activeProfiles", activeProfiles);
        model.addAttribute("inactiveProfiles", inactiveProfiles);
        model.addAttribute("deletedProfiles", deletedProfiles);

        // Nếu có jobPostId, lưu lại trong model để lọc
        if (jobPostId != null) {
            model.addAttribute("selectedJobPostId", jobPostId);
        }
        // Nếu có status, lưu lại trong model để hiển thị trang thái đã chọn
        if (status != null) {
            model.addAttribute("selectedStatus", status);
        }

        return "Company/Hosoungtuyen";
    }



    //    @PreAuthorize("hasRole('ROLE_COMPANY')")
//    @GetMapping
//    public String getApplicationProfile(
//     Model model,
//     Authentication authentication,
//     @RequestParam(defaultValue = "") Long jobPostId,
//     @RequestParam(defaultValue = "1") Integer pageNo
//    ) {
//        User user = (User) authentication.getPrincipal();
//
//        // Lấy danh sách các bài đăng việc làm của công ty
//        List<JobPostTitleResponse> companyJobPosts = jobPostService.getJobPostTitle();
//
//        // Kiểm tra nếu có jobPostId được truyền vào
//        PageResponse<JobProfile> jobProfiles;
//        if (jobPostId != null) {
//            // Lọc hồ sơ theo jobPostId
//            jobProfiles = jobProfileService.getALlProfileByJobPostid(jobPostId,pageNo);
//        } else {
//            // Lấy tất cả hồ sơ của công ty
//            jobProfiles = jobProfileService.getAllProfilesByCompany(user.getCompany(),pageNo);
//        }
//
//        model.addAttribute("jobProfiles", jobProfiles.getContent());
//        model.addAttribute("jobPostsTitle", companyJobPosts);
//        model.addAttribute("totalPages", jobProfiles.getTotalPages());
//        model.addAttribute("currentPage", pageNo);
//
//        // Nếu có jobPostId, thêm vào model để giữ trạng thái lọc
//        if (jobPostId != null) {
//            model.addAttribute("selectedJobPostId", jobPostId);
//        }
//        return "Company/Hosoungtuyen";
//    }



//    @PreAuthorize("hasRole('ROLE_COMPANY')")
//    @GetMapping("/profile/{profileId}")
//    public String getProfileDetails(@PathVariable Long profileId, Model model) {
//        // Lấy chi tiết một hồ sơ cụ thể
//        JobProfile jobProfile = jobProfileService.getJobProfileById(profileId);
//        model.addAttribute("jobProfile", jobProfile);
//
//        return "Company/ChiTietHoSoUngVien";
//    }
//
//    @PreAuthorize("hasRole('ROLE_COMPANY')")
//    @PostMapping("/profile/{profileId}/status")
//    public String updateProfileStatus(
//     @PathVariable Long profileId,
//     @RequestParam String status,
//     RedirectAttributes redirectAttributes
//    ) {
//        try {
//            // Cập nhật trạng thái hồ sơ ứng tuyển
//            jobProfileService.updateProfileStatus(profileId, status);
//            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật trạng thái hồ sơ thành công");
//        } catch (Exception e) {
//            log.error("Lỗi khi cập nhật trạng thái hồ sơ", e);
//            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật trạng thái");
//        }
//
//        return "redirect:/Company/ApplicationProfile/profile/" + profileId;
//    }
}
