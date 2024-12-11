package poly.com.controller.CompanyController2;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.SpringVersion;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.com.Enum.RoleType;
import poly.com.dto.request.accountCompany.CompanyRequest;
import poly.com.dto.request.accountCompany.accountRequest;
import poly.com.dto.response.Auth.AuthenticationResponse;
import poly.com.model.Company;
import poly.com.model.JobCategory;
import poly.com.model.Profile;
import poly.com.repository.JobCategoryRepository;
import poly.com.service.CompanyService;
import poly.com.service.JobCategoryService;
import poly.com.service.ProfileService;
import poly.com.Util.AuthenticationUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/company-account")
@RequiredArgsConstructor
@SessionAttributes("user")
@Slf4j
public class accountCompanyController {

    private final CompanyService companyService;
    private final AuthenticationUtil authenticationUtil;
    private final ProfileService profileService;
    private final JobCategoryService jobCategoryService;
    private final JobCategoryRepository jobCategoryRepository;

    private boolean checkCompanyAccess(AuthenticationResponse user, RedirectAttributes redirectAttributes) {
        if (user == null || !user.getRoles().contains(RoleType.ROLE_COMPANY)) {
            redirectAttributes.addFlashAttribute("error", "Không có quyền truy cập");
            return false;
        }
        return true;

    }


    @GetMapping
    public String showProfileForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {

        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");
        Company company = companyService.findByUserId(user.getId());
        // Lấy danh sách lĩnh vực
        List<JobCategory> jobCategories = jobCategoryRepository.findAll();
        if (!checkCompanyAccess(user, redirectAttributes)) {
            return "redirect:/auth/login";
        }

        model.addAttribute("profile", profileService.findById(user.getId()));
        model.addAttribute("company", companyService.findById(user.getId()));
        model.addAttribute("userEmail", user.getEmail());
        model.addAttribute("jobCategories", jobCategories);
        return "Company/Thongtintaikhoan";
    }

    @PreAuthorize("hasRole('ROLE_COMPANY')")
    @PostMapping("/change-password")
    public String changePassword(
     @RequestParam String currentPassword,
     @RequestParam String newPassword,
     @RequestParam String confirmNewPassword,
     HttpSession session,
     RedirectAttributes redirectAttributes) {
        try {
            AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");

            if (user == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Người dùng chưa đăng nhập.");
                return "redirect:/auth/login";
            }

            // Kiểm tra mật khẩu mới có khớp không
            if (!newPassword.equals(confirmNewPassword)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới không khớp.");
                return "redirect:/company-account";
            }

            // Đổi mật khẩu
            profileService.changePassword(user.getId(), currentPassword, newPassword);

            redirectAttributes.addFlashAttribute("successMessage", "Thay đổi mật khẩu thành công!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Thay đổi mật khẩu thất bại: " + e.getMessage());
        }
        return "redirect:/company-account";
    }

    @PreAuthorize("hasRole('ROLE_COMPANY')")
    @PostMapping("/update-profile")
    public String updateProfile(
     @Valid @ModelAttribute("profile") accountRequest profileDto,
     BindingResult bindingResult,
     @RequestParam(value = "fileLogo", required = false) MultipartFile fileLogo,
     Model model,
     HttpSession session,
     RedirectAttributes redirectAttributes) {

        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");

        if (!checkCompanyAccess(user, redirectAttributes)) {
            return "redirect:/auth/login";
        }

        // Kiểm tra validation
        if (bindingResult.hasErrors()) {
            model.addAttribute("company", companyService.findById(user.getId()));
            model.addAttribute("userEmail", user.getEmail());
            return "Company/Thongtintaikhoan";
        }

        try {
            // Đặt file logo vào request
            profileDto.setFileLogo(fileLogo);

            // Lưu profile
            Profile savedProfile = companyService.saveProfile(profileDto, user.getId());

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");

            return "redirect:/company-account";
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật profile: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật thông tin thất bại: " + e.getMessage());
            return "redirect:/company-account";
        }
    }

    @PreAuthorize("hasRole('ROLE_COMPANY')")
    @PostMapping("/update-company")
    public String updateCompany(
     @Valid @ModelAttribute CompanyRequest companyRequest,
     BindingResult bindingResult,
     HttpSession session,
     RedirectAttributes redirectAttributes) {
        // Lấy user hiện tại từ session
        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");

        // Kiểm tra quyền truy cập
        if (user == null || !user.getRoles().contains(RoleType.ROLE_COMPANY)) {
            redirectAttributes.addFlashAttribute("error", "Không có quyền truy cập");
            return "redirect:/auth/login";
        }

        // Kiểm tra validation
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errors", bindingResult.getFieldErrors());
            return "redirect:/company-account";
        }

        try {
            // Đặt userId từ session
            companyRequest.setUserId(user.getId());

            // Gọi service để lưu thông tin công ty
            Company savedCompany = companyService.save(companyRequest);

            // Thêm thông báo thành công
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin công ty thành công!");

            return "redirect:/company-account";
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật thông tin công ty: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Có lỗi xảy ra khi cập nhật thông tin: " + e.getMessage());
            return "redirect:/company-account";
        }
    }

    // Endpoint để lấy thông tin công ty của user hiện tại
    @PreAuthorize("hasRole('ROLE_COMPANY')")
    @GetMapping("/get-company")
    public ResponseEntity<?> getCompanyByUserId(HttpSession session) {
        try {
            // Lấy user hiện tại từ authentication
            AuthenticationResponse user =
             (AuthenticationResponse) session.getAttribute("user");

            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                 .body("Người dùng chưa được xác thực");
            }

            // Tìm và trả về thông tin công ty
            Company company = companyService.findByUserId(user.getId());

            return ResponseEntity.ok(company);
        } catch (Exception e) {
            log.error("Lỗi khi lấy thông tin công ty: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
             .body("Có lỗi xảy ra khi lấy thông tin: " + e.getMessage());
        }
    }



    //    @PreAuthorize("hasRole('ROLE_COMPANY')")
    //    @GetMapping
    //    public String showAccountPage(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
    //        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");
    //        System.out.println("==========================================user qua ben company-account : " + user);
    //
    //        if (!checkCompanyAccess(user, redirectAttributes)) {
    //            return "redirect:/auth/login";
    //        }
    //
    //        var currentUser = authenticationUtil.getCurrentUser();
    //        model.addAttribute("user", currentUser);
    //        System.out.println("==========================================currentUser : " + currentUser);
    //        model.addAttribute("profile", currentUser.getProfiles());
    //        System.out.println("==========================================currentUser.getProfiles() : " + currentUser.getProfiles());
    //        model.addAttribute("company", currentUser.getCompany());
    //        System.out.println("==========================================currentUser.getCompany() : " + currentUser.getCompany());
    //
    //        return "Company/thongtinthanhtoan";
    //    }

    //    @PreAuthorize("hasRole('ROLE_COMPANY')")
    //    @PostMapping("/update-profile")
    //    public String updateProfile(
    //     @ModelAttribute accountRequest request,
    //     @RequestParam(value = "fileLogo", required = false) MultipartFile fileLogo,
    //     HttpSession session,
    //     RedirectAttributes redirectAttributes) {
    //        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");
    //
    //        if (!checkCompanyAccess(user, redirectAttributes)) {
    //            return "redirect:/auth/login";
    //        }
    //
    //        try {
    //            request.setFileLogo(fileLogo);
    //            Profile updatedProfile = companyService.save(request);
    //            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin tài khoản thành công!");
    //            return "redirect:/company-account";
    //        } catch (Exception e) {
    //            log.error("Lỗi cập nhật profile: ", e);
    //            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật thông tin thất bại: " + e.getMessage());
    //            return "redirect:/company-account";
    //        }
    //    }
    //
    //    @PreAuthorize("hasRole('ROLE_COMPANY')")
    //    @PostMapping("/update-company")
    //    public String updateCompany(
    //     @ModelAttribute CompanyRequest request,
    //     @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
    //     HttpSession session,
    //     RedirectAttributes redirectAttributes) {
    //        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");
    //
    //        if (!checkCompanyAccess(user, redirectAttributes)) {
    //            return "redirect:/auth/login";
    //        }
    //
    //        try {
    //            request.setLogoFile(logoFile);
    //            Company updatedCompany = companyService.save(request);
    //            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin công ty thành công!");
    //            return "redirect:/company-account";
    //        } catch (Exception e) {
    //            log.error("Lỗi cập nhật thông tin công ty: ", e);
    //            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật thông tin thất bại: " + e.getMessage());
    //            return "redirect:/company-account";
    //        }
    //    }
}