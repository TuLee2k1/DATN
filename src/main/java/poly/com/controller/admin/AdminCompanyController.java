package poly.com.controller.admin;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import poly.com.dto.CompanyDto;
import poly.com.model.Company;
import poly.com.model.JobPost;
import poly.com.service.CompanyService;
import poly.com.service.FileStorageService;
import poly.com.service.MapValidationErrorService;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("admin/companies")
public class AdminCompanyController {
    @Autowired
    CompanyService companyService;

    @Autowired
    MapValidationErrorService mapValidationErrorService;

    @Autowired
    private FileStorageService fileStorageService;

    @Operation(summary = "Get All Company", description = "API get all company")

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public String listAll(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Page<Company> companies = companyService.companyPage(pageNo);

        model.addAttribute("companies", companies);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", companies.getTotalPages());
        return "admin/companies/company";
    }
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("details/{id}")
    public String getCompanyDetails(@PathVariable("id") Long id, Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Company company = companyService.findById(id);
        if (company == null) {
            model.addAttribute("error", "JobPost not found!");
            return "error"; // Hoặc một trang báo lỗi phù hợp
        }
        model.addAttribute("company", company);
        return "/admin/companies/companies_details";
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("delete/{id}")
    public ModelAndView delete(ModelMap model,  @PathVariable("id") Long id ) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        companyService.deleteById(id);

        model.addAttribute("message", "Company is deleted!");


        return new ModelAndView("forward:/admin/companies", model);
    }



    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/images/{fileName:.+}")
    public ModelAndView deleteLogoCompany(ModelMap model,@PathVariable String fileName){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        fileStorageService.deleteCompanyImageFile(fileName);

        return new ModelAndView("forward:/admin/companies", model);

    }

}
