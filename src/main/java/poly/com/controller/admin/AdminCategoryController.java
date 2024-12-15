package poly.com.controller.admin;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import poly.com.dto.JobCategoryDto;
import poly.com.model.JobCategory;
import poly.com.model.SubCategory;
import poly.com.repository.SubCategoryRepository;
import poly.com.service.JobCategoryService;
import poly.com.service.JobPostService;
import poly.com.service.MapValidationErrorService;
import poly.com.service.SubCategoryService;

import java.util.List;

@Controller
@RequestMapping("admin/categories")
public class AdminCategoryController {
    @Autowired
    JobCategoryService jobCategoryService;

    @Autowired
    SubCategoryRepository subCategoryRepository;

    @Autowired
    SubCategoryService subCategoryService;

    @Autowired
    MapValidationErrorService mapValidationErrorService;

    @Autowired
    JobPostService jobPostService;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping
    public String listAll(ModelMap model,@RequestParam(defaultValue = "1") Integer pageNo,@RequestParam(defaultValue = "1") Integer pageNo2) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Page<JobCategory> list = jobCategoryService.getJobCategories(pageNo);
        model.addAttribute("categories", list);
        model.addAttribute("currentPageC", pageNo);
        model.addAttribute("totalPagesC", list.getTotalPages());

        Page<SubCategory> listSubCategory = subCategoryService.getAllSubCategories(pageNo2);
        model.addAttribute("subCategories", listSubCategory);
        model.addAttribute("currentPageS", pageNo2);
        model.addAttribute("totalPagesS", listSubCategory.getTotalPages());

        List<JobCategory> Allcategories = jobCategoryService.getAllJobCategories();
        model.addAttribute("Allcategories", Allcategories);
        return "admin/categories/category";
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add")
    public String addCategory(@RequestParam("categoryName") String categoryName, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (categoryName == null || categoryName.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Category name cannot be empty.");
            return "redirect:/admin/categories";
        }

        JobCategory newCategory = new JobCategory();
        newCategory.setCategoryName(categoryName);

        jobCategoryService.save(newCategory);
        redirectAttributes.addFlashAttribute("message", "Danh mục đã được thêm thành công!");

        return "redirect:/admin/categories"; // Quay lại trang danh sách
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("delete/{id}")
    public ModelAndView delete(ModelMap model, @PathVariable("id") Long id , RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        boolean isUsedInJobPosts = jobPostService.isJobCategoryUsed(id);
        boolean isUsedInSubCategory = subCategoryService.isJobCategoryUsedInSubCategory(id);

        ModelAndView modelAndView = new ModelAndView("redirect:/admin/categories");

        if (isUsedInJobPosts) {
            // Nếu danh mục công việc đang được sử dụng, trả về thông báo lỗi
            redirectAttributes.addFlashAttribute("error", "Danh mục công việc này đang được sử dụng cho các bài đăng, không thể xóa!");
            return modelAndView;
        }


        if (isUsedInSubCategory) {
            // Nếu job_category đang được sử dụng trong sub_category, trả về thông báo lỗi
            redirectAttributes.addFlashAttribute("error", "Danh mục công việc này đang được chứa danh mục con, vui lòng xóa danh mục con trước!");
            return modelAndView;
        }

        jobCategoryService.delete(id);

        model.addAttribute("message", "Category is deleted!");


        return new ModelAndView("forward:/admin/categories", model);
    }


    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/add_subcategory")
    public String addSubCategory(@ModelAttribute SubCategory subCategory, RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        try {
            // Lưu SubCategory vào database
            subCategoryRepository.save(subCategory);
            redirectAttributes.addFlashAttribute("message", "Danh mục con đã được thêm thành công!!");

            return "redirect:/admin/categories"; // Quay lại danh sách SubCategory
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to add SubCategory.");
            return "admin/categories/category"; // Trả về trang thêm SubCategory nếu có lỗi
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("deleteSub/{id}")
    public ModelAndView deleteSubCategory(@PathVariable("id") Long id , RedirectAttributes redirectAttributes) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra xem danh mục có đang được sử dụng trong các bài đăng không
        boolean isUsedInJobPosts = jobPostService.isSubCategoryUsed(id);// Bạn cần tạo phương thức này trong jobPostService để kiểm tra

        ModelAndView modelAndView = new ModelAndView("redirect:/admin/categories");

        if (isUsedInJobPosts) {
            redirectAttributes.addFlashAttribute("error", "Danh mục này đang được sử dụng cho các bài đăng, vui lòng không xóa!");
            return modelAndView;
        }

        // Nếu không có bài đăng nào sử dụng, tiến hành xóa
        subCategoryService.delete(id);

        // Thêm thông báo thành công
        redirectAttributes.addFlashAttribute("message", "Xóa danh mục thành công!");

        // Chuyển hướng sau khi xóa thành công
        return modelAndView;
    }

}



