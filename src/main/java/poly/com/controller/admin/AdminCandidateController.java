package poly.com.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
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

    @RequestMapping("")
    public String listAll(ModelMap model){
        List<Profile> candidate = profileService.findAll();
        model.addAttribute("candidates", candidate);
        return "admin/candidates/candidate";
    }

    @GetMapping("delete/{id}")
    public ModelAndView delete(ModelMap model, @PathVariable("id") Long id ) {

        profileService.deleteById(id);

        model.addAttribute("message", "Candidate is deleted!");


        return new ModelAndView("forward:/admin/candidates", model);
    }



    @DeleteMapping("/images/{fileName:.+}")
    public ModelAndView deleteAvatar(ModelMap model,@PathVariable String fileName){

        fileStorageService.deleteProfileImageFile(fileName);

        return new ModelAndView("forward:/admin/candidates", model);

    }
}
