package poly.com.controller.CompanyController2;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import poly.com.Enum.StatusEnum;
import poly.com.util.AuthenticationUtil;
import poly.com.util.ExcelUtils;
import poly.com.dto.request.JobPost.JobPostTitleResponse;
import poly.com.dto.response.PageResponse;
import poly.com.model.JobProfile;
import poly.com.model.User;
import poly.com.service.JobPostService;
import poly.com.service.JobProfileService;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

@Controller
@RequiredArgsConstructor
@RequestMapping("/Company/ApplicationProfile")
@Slf4j
@SessionAttributes("user")
public class ApplicationProfileController {

    private final JobProfileService jobProfileService;
    private final JobPostService jobPostService;
    private final ExcelUtils excelUtils;
    private final AuthenticationUtil authenticationUtil;

    @PreAuthorize("hasRole('ROLE_COMPANY')")
@GetMapping
public String getApplicationProfile(
 Model model,
 Authentication authentication,
 @RequestParam(defaultValue = "") Long jobPostId,
 @RequestParam(defaultValue = "1") Integer pageNo,
 @RequestParam(defaultValue = "") StatusEnum status // Thêm tham số trạng thái vào URL
) {

    User user = (User) authentication.getPrincipal();

    // Lấy danh sách các bài đăng công việc
    List<JobPostTitleResponse> companyJobPosts = jobPostService.getJobPostTitle();


    model.addAttribute("statusEnum", StatusEnum.values());
        // Nếu có jobPostId, lưu lại trong model để lọc
        if (jobPostId != null) {
            model.addAttribute("selectedJobPostId", jobPostId);
        }
        // Nếu có status, lưu lại trong model để hiển thị trang thái đã chọn
        if (status != null) {
            model.addAttribute("selectedStatus", status);
        }

    // Fetch job profiles based on jobPostId and status
    PageResponse<JobProfile> jobProfiles;
    if (jobPostId != null && status != null) {
        // Nếu có jobPostId và status, lọc theo jobPostId và status
        jobProfiles = jobProfileService.getAllProfilesByJobPostAndStatus(jobPostId, status, pageNo);
    } else if (jobPostId != null) {
        // Nếu chỉ có jobPostId, lấy tất cả hồ sơ theo jobPostId
        jobProfiles = jobProfileService.getALlProfileByJobPostid(jobPostId, pageNo);

    }else if (status != null) {
        // Nếu chỉ có status, lấy tất cả hồ sơ theo status
        jobProfiles = jobProfileService.getJobPliesByStatus(status, pageNo);
    }
    else {
        // Nếu không có jobPostId, lấy tất cả hồ sơ của công ty
        jobProfiles = jobProfileService.getAllProfilesByCompany(user.getCompany(), pageNo);
    }
        Long totalProfiles = jobProfileService.countTotalProfilesByJobPost(jobPostId);
        Long pendingProfiles = jobProfileService.countProfilesByJobPostAndStatus(jobPostId, StatusEnum.PENDING);
        Long verifiedProfiles = jobProfileService.countProfilesByJobPostAndStatus(jobPostId, StatusEnum.VERIFIED);
        Long rejectedProfiles = jobProfileService.countProfilesByJobPostAndStatus(jobPostId, StatusEnum.REJECTED);
        Long activeProfiles = jobProfileService.countProfilesByJobPostAndStatus(jobPostId, StatusEnum.ACTIVE);
        Long inactiveProfiles = jobProfileService.countProfilesByJobPostAndStatus(jobPostId, StatusEnum.INACTIVE);
        Long deletedProfiles = jobProfileService.countProfilesByJobPostAndStatus(jobPostId, StatusEnum.DELETED);


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



    return "Company/Hosoungtuyen";
}

    @GetMapping("/export")
    public void exportJobProfiles(
     HttpServletResponse response,
     @RequestParam(defaultValue = "") Long jobPostId,
     @RequestParam(defaultValue = "") StatusEnum status
    ) {
        try {
            // Lấy người dùng hiện tại
            User user = authenticationUtil.getCurrentUser();
            // Lấy danh sách hồ sơ
            // Lấy danh sách hồ sơ không phân trang
            List<JobProfile> jobProfiles;

            if (jobPostId != null && status != null) {
                // Nếu có jobPostId và status, lọc theo jobPostId và status
                jobProfiles = jobProfileService.getProfilesByJobPostAndStatus(jobPostId, status);
            } else if (jobPostId != null) {
                // Nếu chỉ có jobPostId, lấy tất cả hồ sơ theo jobPostId
                jobProfiles = jobProfileService.getProfilesByJobPost(jobPostId);
            } else if (status != null) {
                // Nếu chỉ có status, lấy tất cả hồ sơ theo status
                jobProfiles = jobProfileService.getProfilesByStatus(status);
            } else {
                // Nếu không có jobPostId, lấy tất cả hồ sơ của công ty
                jobProfiles = jobProfileService.getProfilesByCompany(user.getCompany());
            }


            // Định nghĩa headers
            String[] headers = {
             "Mã Hồ Sơ",
             "Tên Ứng Viên",
             "Email",
             "Số Điện Thoại",
             "Vị Trí Ứng Tuyển",
             "Trạng Thái",
             "Ngày Nộp"
            };

            // Mapper dữ liệu
            Function<JobProfile, Object[]> dataMapper = profile -> new Object[]{
             profile.getId(),
             profile.getFullName(),
             profile.getEmail(),
             profile.getPhoneNumber(),
             profile.getJobPost() != null ? profile.getJobPost().getJobTitle() : "N/A",
             profile.getStatus(),
             profile.getDateApply()
            };

            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
         //   response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''");

            // Xuất Excel trực tiếp vào output stream của response
            ServletOutputStream outputStream = response.getOutputStream();

            // Xuất Excel
            byte[] excelContent = excelUtils.exportToExcel(jobProfiles, headers, dataMapper, "Danh sách hồ sơ ứng tuyển");

            // Ghi file Excel vào response
            outputStream.write(excelContent);
            outputStream.flush();
            outputStream.close();

        } catch (Exception e) {
            System.out.println("Lỗi khi xuất Excel"+e);
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi xuất Excel");
            } catch (IOException ioException) {
                System.out.println("Lỗi khi xuất Excel"+ioException);
            }
        }
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
