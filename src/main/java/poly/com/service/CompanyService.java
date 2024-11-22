package poly.com.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.com.dto.CompanyDto;
import poly.com.exception.CompanyException;
import poly.com.model.Company;
import poly.com.repository.CompanyRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CompanyService {

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    private FileStorageService fileStorageService;


    public  Company  save(@Valid CompanyDto dto) {

        List<?> foudedList = companyRepository.findByNameContainsIgnoreCase(dto.getName());

        if (foudedList.size()>0) {
            throw new CompanyException("Company name already exist");
        }

        Company entity = new Company();

        BeanUtils.copyProperties(dto, entity);

        if (dto.getLogoFile() != null){
            String fileName = fileStorageService.storeImageCompanyFile(dto.getLogoFile());
            entity.setLogo(fileName);
            dto.setLogo(null);
        }
        return companyRepository.save(entity);
    }


    public  Company  update(Long id, Company entity) {
        Optional<Company> existed = companyRepository.findById(id);

        if (existed.isEmpty()) {
            throw new CompanyException("Company "  +  id  + " not found");
        }
        try {
           Company  existedCompany = existed.get();
           existedCompany.setName(entity.getName());
           existedCompany.setAddress(entity.getAddress());
           existedCompany.setPhone(entity.getPhone());
           existedCompany.setEmail(entity.getEmail());
           existedCompany.setLogo(entity.getLogo());

           return companyRepository.save(existedCompany);

        }catch (Exception ex){

            throw new CompanyException("Company is update fail");
        }
    }


    public List<Company> findAll() {
        return companyRepository.findAll();
    }


    public Page<Company> findAll(Pageable pageable) {
        return companyRepository.findAll(pageable);
    }


    public Company findById(Long id) {
        Optional<Company> found = companyRepository.findById(id);

        if (found.isEmpty()) {
            throw new EntityNotFoundException("Company id " + id + " not found");
        }
        return found.get();
    }


    public void deleteById(Long id) {
        Company existed = findById(id);

        companyRepository.delete(existed);
    }

}

