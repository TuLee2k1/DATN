package poly.com.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.com.dto.ProfileDTO;
import poly.com.dto.response.Auth.AuthenticationResponse;
import poly.com.model.Profile;
import poly.com.service.ProfileService;

@Controller
@RequestMapping("/user-profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;

    // Hiển thị trang profile
    @GetMapping("")
    public String showProfilePage(HttpSession session, Model model) {
        // Lấy thông tin user từ session
        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        // Lấy thông tin profile
        Profile profile = profileService.findById(user.getId());

        // Chuyển đổi sang DTO để sử dụng trong form
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setId(profile.getId());
        profileDTO.setName(profile.getName());
        profileDTO.setEmail(profile.getEmail());
        profileDTO.setPhone(profile.getPhone());
        profileDTO.setAddress(profile.getAddress());
        profileDTO.setSex(profile.getSex());
        profileDTO.setDateOfBirth(profile.getDateOfBirth());
        profileDTO.setLogo(profile.getLogo());

        model.addAttribute("profileDTO", profileDTO);
        return "User/V3/profile";
    }

    // Cập nhật profile
    @PostMapping("/update")
    public String updateProfile(
            @Valid @ModelAttribute("profileDTO") ProfileDTO profileDTO,
            BindingResult bindingResult,
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
            HttpSession session,
            RedirectAttributes redirectAttributes,
            Model model) {

        // Lấy thông tin user từ session
        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");

        if (user == null) {
            return "redirect:/auth/login";
        }

        // Kiểm tra validation
        if (bindingResult.hasErrors()) {
            model.addAttribute("profileDTO", profileDTO);
            return "User/V3/profile";
        }

        try {
            // Đặt file logo vào DTO
            profileDTO.setLogoFile(logoFile);

            // Cập nhật profile
            Profile updatedProfile = profileService.updateProfile(user.getId(), profileDTO);

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
            return "redirect:/user-profile";
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật profile: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật thông tin thất bại: " + e.getMessage());
            return "redirect:/user-profile";
        }
    }
}