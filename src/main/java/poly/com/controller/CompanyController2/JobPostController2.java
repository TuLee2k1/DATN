    package poly.com.controller.CompanyController2;
    
    import jakarta.servlet.http.HttpSession;
    import jakarta.validation.Valid;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.stereotype.Controller;
    import org.springframework.ui.Model;
    import org.springframework.validation.BindingResult;
    import org.springframework.web.bind.annotation.*;
    import org.springframework.web.servlet.mvc.support.RedirectAttributes;
    import poly.com.Enum.RoleType;
    import poly.com.dto.request.JobPost.JobPostRequest;
    import poly.com.dto.response.Auth.AuthenticationResponse;
    import poly.com.dto.response.JobPost.JobPostResponse;
    import poly.com.service.JobCategoryService;
    import poly.com.service.JobPostService;
    import poly.com.service.SubCategoryService;
    
    @Controller
    @RequiredArgsConstructor
    @RequestMapping("/job-posts")
    @Slf4j
    @SessionAttributes("user")
    public class JobPostController2 {
    
        private final JobPostService jobPostService;
        private final JobCategoryService jobCategoryService;
        private final SubCategoryService subCategoryService;
    
        @PreAuthorize("hasRole('ROLE_COMPANY')")
        @GetMapping()
        public String getJobPostPage(HttpSession session, Model model) {
            AuthenticationResponse user = (AuthenticationResponse) session.getAttribute("user");
            System.out.println("Authen get JobPostController2: " + user);
    
            // Kiểm tra quyền truy cập
            if (user == null || !user.getRoles().contains(RoleType.ROLE_COMPANY)) {
                return "redirect:/auth/login";
            }
    
            model.addAttribute("user", user);
            model.addAttribute("jobPostRequest", new JobPostRequest());
            model.addAttribute("jobCategories", jobCategoryService.getAllJobCategories());
            model.addAttribute("subCategories", subCategoryService.getAllSubCategories());
            return "Company/Taotintuyendung";
        }
    
        @PreAuthorize("hasRole('ROLE_COMPANY')")
        @PostMapping("/create")
        public String createJobPost(
         @Valid @ModelAttribute("jobPostRequest") JobPostRequest request,
         BindingResult bindingResult,
         RedirectAttributes redirectAttributes,
         Model model,
         HttpSession session
        ) {
            try {
                // Lấy user từ session
                AuthenticationResponse authUser = (AuthenticationResponse) session.getAttribute("user");
    
                if (authUser == null) {
                    return "redirect:/auth/login";
                }
    
                // Kiểm tra role
                if (!authUser.getRoles().contains(RoleType.ROLE_COMPANY)) {
                    redirectAttributes.addFlashAttribute("error", "Không có quyền truy cập");
                    return "redirect:/auth/login";
                }
    
                // Xử lý validation errors
                if (bindingResult.hasErrors()) {
                    prepareModelForForm(model, authUser);
                    return "Company/Taotintuyendung";
                }

                // Set user ID vào request
                request.setUserId(authUser.getId());
                request.setUserEmail(authUser.getEmail());
    
                // Tạo job post
                JobPostResponse response = jobPostService.createJobPost(request);
    
                redirectAttributes.addFlashAttribute("successMessage", "Tạo bài đăng việc làm thành công!");
                System.out.println("Tạo bài đăng việc làm thành công!");
                return "redirect:/job-posts";
    
            } catch (Exception e) {
                log.error("Lỗi khi tạo job post: ", e);
                AuthenticationResponse authUser = (AuthenticationResponse) session.getAttribute("user");
                prepareModelForForm(model, authUser);
                model.addAttribute("errorMessage", "Có lỗi xảy ra: " + e.getMessage());
                return "Company/Taotintuyendung";
            }
        }
    
        private void prepareModelForForm(Model model, AuthenticationResponse user) {
            model.addAttribute("user", user);
            model.addAttribute("jobCategories", jobCategoryService.getAllJobCategories());
            model.addAttribute("subCategories", subCategoryService.getAllSubCategories());
        }
    
    
    
    
        @GetMapping("/detail/{id}")
        public String showJobPostDetail(@PathVariable Long id, Model model) {
            JobPostResponse jobPost = jobPostService.getJobPost(id);
            model.addAttribute("jobPost", jobPost);
            return "job-post/detail";
        }
    }