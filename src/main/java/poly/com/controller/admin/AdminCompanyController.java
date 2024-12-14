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
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import poly.com.dto.CompanyDto;
import poly.com.model.Company;
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

    @RequestMapping("")
    public String listAll(@RequestParam(defaultValue = "1") Integer pageNo, Model model){
        Page<Company> companies = companyService.companyPage(pageNo);

        model.addAttribute("companies", companies);

        model.addAttribute("currentPage", pageNo);
        model.addAttribute("totalPages", companies.getTotalPages());
        return "admin/companies/company";
    }

    @GetMapping("delete/{id}")
    public ModelAndView delete(ModelMap model,  @PathVariable("id") Long id ) {

        companyService.deleteById(id);

        model.addAttribute("message", "Company is deleted!");


        return new ModelAndView("forward:/admin/companies", model);
    }



    @DeleteMapping("/images/{fileName:.+}")
    public ModelAndView deleteLogoCompany(ModelMap model,@PathVariable String fileName){

        fileStorageService.deleteCompanyImageFile(fileName);

        return new ModelAndView("forward:/admin/companies", model);

    }

}
