package poly.com.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import poly.com.model.Company;
import poly.com.model.Profile;
import poly.com.service.FileStorageService;
import poly.com.service.MapValidationErrorService;
import poly.com.service.ProfileService;

import java.util.List;

@Controller
@RequestMapping("admin/candidates")
public class AdminCandidateController {

    @Autowired
    ProfileService profileService;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    MapValidationErrorService mapValidationErrorService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public String listAll(ModelMap model,@RequestParam(defaultValue = "1") Integer pageNo){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Page<Profile> candidate = profileService.getAllByAdmin(pageNo);
        model.addAttribute("candidates", candidate);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", candidate.getTotalPages());
        return "admin/candidates/candidate";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("delete/{id}")
    public ModelAndView delete(ModelMap model, @PathVariable("id") Long id ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        profileService.deleteById(id);

        model.addAttribute("message", "Candidate is deleted!");


        return new ModelAndView("forward:/admin/candidates", model);
    }



    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/images/{fileName:.+}")
    public ModelAndView deleteAvatar(ModelMap model,@PathVariable String fileName){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();


        fileStorageService.deleteProfileImageFile(fileName);

        return new ModelAndView("forward:/admin/candidates", model);

    }
}
