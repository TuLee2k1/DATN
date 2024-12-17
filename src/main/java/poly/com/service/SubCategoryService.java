package poly.com.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.com.exception.JobCategoryException;
import poly.com.model.JobCategory;
import poly.com.model.SubCategory;
import poly.com.repository.SubCategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubCategoryService {

    @Autowired
    private final SubCategoryRepository subCategoryRepository;

    public List<SubCategory> getAllSubCategories() {
        return subCategoryRepository.findAll();
    }

    public List<SubCategory> getSubCategoriesByJobCategoryId(Long jobCategoryId) {
        return subCategoryRepository.findByJobCategoryId(jobCategoryId);


    public Page<SubCategory> getAllSubCategories(Integer pageNo2) {
        Pageable pageable = PageRequest.of(pageNo2 - 1, 3);
        return subCategoryRepository.findAll(pageable);
    }

    public SubCategory findById(Long id) {
        Optional<SubCategory> found = subCategoryRepository.findById(id);
        if (found.isEmpty()) {
            throw new JobCategoryException("Sub Category id " + id + " not found!");
        }
        return found.get();
    }

    public void delete(Long id) {
        SubCategory existed = findById(id);

        subCategoryRepository.delete(existed);
    }

    // Kiểm tra xem job_category_id có đang được sử dụng trong các sub_category không
    public boolean isJobCategoryUsedInSubCategory(Long jobCategoryId) {
        return subCategoryRepository.existsByJobCategoryId(jobCategoryId);

    }
}
