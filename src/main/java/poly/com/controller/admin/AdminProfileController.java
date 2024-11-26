package poly.com.controller.admin;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import poly.com.model.JobCategory;
import poly.com.model.User;
import poly.com.service.AuthenticationService;
import poly.com.service.MapValidationErrorService;

import java.util.List;

@Controller
@RequestMapping("admin/profiles")
public class AdminProfileController {
    @Autowired
    AuthenticationService authenticationService;

    @Autowired
    MapValidationErrorService mapValidationErrorService;

    @RequestMapping
    public String getAllProfiles(ModelMap model) {


        return "admin/profiles/profile";
    }
}
