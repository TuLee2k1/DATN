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
import poly.com.dto.request.profileRequest;
import poly.com.dto.response.Auth.AuthenticationResponse;
import poly.com.model.Profile;
import poly.com.service.ProfileService;

@Controller
@RequestMapping("/user-profile")
@RequiredArgsConstructor
@Slf4j
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public String showProfileForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập");
            return "redirect:/auth/login";
        }

        try {
            Profile profile = profileService.findById(user.getId());
            System.out.println(user.getId());
            model.addAttribute("profile", profile);
            return "User/V3/profile"; // Tên view mới
        } catch (Exception e) {
            // Nếu chưa có profile, khởi tạo một profile mới
            Profile newProfile = new Profile();
            newProfile.setId(user.getId());
            model.addAttribute("profile", newProfile);
            return "User/V3/profile";
        }
    }

    @PostMapping("/update")
    public String updateProfile(
            @Valid @ModelAttribute("profile") profileRequest profileDto,
            BindingResult bindingResult,
            @RequestParam(value = "logoFile", required = false) MultipartFile logoFile,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập");
            return "redirect:/auth/login";
        }

        // Kiểm tra validation
        if (bindingResult.hasErrors()) {
            return "User/V3/profile";
        }

        try {
            // Đặt file logo vào request
            profileDto.setLogoFile(logoFile);

            // Lưu profile
            Profile savedProfile = profileService.saveProfile(profileDto, user.getId());

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật thông tin thành công!");
            return "redirect:/user-profile";
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật profile: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật thông tin thất bại: " + e.getMessage());
            return "redirect:/user-profile";
        }
    }
}