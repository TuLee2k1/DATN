package poly.com.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.com.dto.request.ResumeRequest;
import poly.com.dto.response.Auth.AuthenticationResponse;
import poly.com.service.resumeService;

@Controller
@RequestMapping("/user-resume")
@RequiredArgsConstructor
@Slf4j
public class resumeController {

    private final resumeService resumeService;

    @GetMapping
    public String showResumeForm(Model model, HttpSession session, RedirectAttributes redirectAttributes) {
        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập");
            return "redirect:/auth/login";
        }

        try {
            ResumeRequest resumeRequest = resumeService.getResumeByUserId(user.getId());
            model.addAttribute("resumeRequest", resumeRequest);
            return "User/V3/resume_"; // Tên view mới
        } catch (Exception e) {
            // Nếu chưa có resume, khởi tạo một resume mới
            model.addAttribute("resumeRequest", new ResumeRequest());
            return "User/V3/resume_";
        }
    }

    @PostMapping("/update")
    public String updateResume(
            @Valid @ModelAttribute("resumeRequest") ResumeRequest resumeRequest,
            BindingResult bindingResult,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");

        if (user == null) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng đăng nhập");
            return "redirect:/auth/login";
        }

        // Kiểm tra validation
        if (bindingResult.hasErrors()) {
            return "User/V3/resume_";
        }

        try {
            // Lưu resume
            resumeService.saveOrUpdateResume(resumeRequest, user.getId());

            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật resume thành công!");
            return "redirect:/user-resume";
        } catch (Exception e) {
            log.error("Lỗi khi cập nhật resume: ", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Cập nhật resume thất bại: " + e.getMessage());
            return "redirect:/user-resume";
        }
    }
}