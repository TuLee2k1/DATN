package poly.com.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.com.model.SubCategory;
import poly.com.repository.SubCategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubCategoryService {

    private final SubCategoryRepository subCategoryRepository;

    public List<SubCategory> getAllSubCategories() {
        return subCategoryRepository.findAll();
    }
}
