package poly.com.controller.admin;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import poly.com.dto.JobCategoryDto;
import poly.com.model.JobCategory;
import poly.com.service.JobCategoryService;
import poly.com.service.MapValidationErrorService;

import java.util.List;

@Controller
@RequestMapping("admin/categories")
public class AdminCategoryController {
    @Autowired
    JobCategoryService jobCategoryService;

    @Autowired
    MapValidationErrorService mapValidationErrorService;

    @RequestMapping
    public String listAll(ModelMap model) {
        List<JobCategory> list = jobCategoryService.findAll();

        model.addAttribute("categories", list);

        return "admin/categories/category";
    }

    @PostMapping("/add")
    public String addCategory(@RequestParam("categoryName") String categoryName, Model model) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            model.addAttribute("error", "Category name cannot be empty.");
            return "redirect:/admin/categories";
        }

        JobCategory newCategory = new JobCategory();
        newCategory.setCategoryName(categoryName);

        jobCategoryService.save(newCategory);

        return "redirect:/admin/categories"; // Quay lại trang danh sách
    }


    @GetMapping("delete/{id}")
    public ModelAndView delete(ModelMap model,  @PathVariable("id") Long id ) {

        jobCategoryService.delete(id);

        model.addAttribute("message", "Category is deleted!");


        return new ModelAndView("forward:/admin/categories", model);
    }






}
