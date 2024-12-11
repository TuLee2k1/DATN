package poly.com.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import poly.com.dto.CompanyDto;
import poly.com.dto.request.accountCompany.CompanyRequest;
import poly.com.dto.request.accountCompany.accountRequest;
import poly.com.exception.CompanyException;
import poly.com.exception.FileStorageException;
import poly.com.model.Company;
import poly.com.model.JobCategory;
import poly.com.model.Profile;
import poly.com.repository.CompanyRepository;
import poly.com.repository.JobCategoryRepository;
import poly.com.repository.ProfileRepository;
import poly.com.util.AuthenticationUtil;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final ProfileRepository profileRepository;
    private final FileStorageService fileStorageService;
    private final AuthenticationUtil authenticationUtil;
    private final JobCategoryRepository jobCategoryRepository;

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
//
//
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

    public Company findByUserId(Long userId) {
        // Tìm kiếm công ty theo userId
        return companyRepository.findByUser_Id(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Không tìm thấy công ty cho người dùng với ID: " + userId
                ));
    }




    public Profile saveProfile(@Valid accountRequest request, Long id) throws IllegalAccessException {
        // Lấy user hiện tại từ authentication
        var currentUser = authenticationUtil.getCurrentUser();

        // Tìm profile hiện tại hoặc tạo mới
        Profile profile = profileRepository.findById(currentUser.getId())
                .orElse(new Profile());

        // Xử lý upload logo nếu có
        if (request.getFileLogo() != null && !request.getFileLogo().isEmpty()) {
            try {
                if (profile.getLogo() != null) {
                    fileStorageService.deleteProfileImageFile(profile.getLogo());
                }

                String logoUrl = fileStorageService.storeImageProfileFile(request.getFileLogo());
                profile.setLogo(logoUrl);
            } catch (Exception e) {
                throw new FileStorageException("Không thể lưu ảnh đại diện", e);
            }
        }

        // Cập nhật thông tin profile
        profile.setName(request.getName());
        profile.setPhone(request.getPhone());
        profile.setAddress(request.getAddress());
        profile.setUser_id(currentUser);

        // Lưu và trả về profile
        return profileRepository.save(profile);
    }


    public Company save(@Valid CompanyRequest request) {
        // Lấy user hiện tại từ authentication
        var currentUser = authenticationUtil.getCurrentUser();

        // Tìm company hiện tại hoặc tạo mới
        Company company = companyRepository.findById(currentUser.getCompany().getId())
                .orElse(new Company());

        // Xử lý upload logo nếu có
        if (request.getLogoFile() != null && !request.getLogoFile().isEmpty()) {
            try {
                // Nếu đã có logo cũ, xóa logo cũ trước khi lưu logo mới
                if (company.getLogo() != null) {
                    fileStorageService.deleteCompanyImageFile(company.getLogo());
                }

                // Lưu logo mới
                String logoUrl = fileStorageService.storeImageCompanyFile(request.getLogoFile());
                company.setLogo(logoUrl);
            } catch (Exception e) {
                // Xử lý ngoại lệ nếu có lỗi khi upload
                throw new FileStorageException("Không thể lưu logo công ty", e);
            }
        }

        // Xử lý upload giấy phép kinh doanh
        String licenseUrl = null;
        if (request.getBusinessLicense() != null && !request.getBusinessLicense().isEmpty()) {
            licenseUrl = fileStorageService.storeImageCompanyFile(request.getBusinessLicense());
        }

        // Cập nhật thông tin công ty
        company.setName(request.getName());
        company.setPhone(request.getPhone());
        company.setEmail(request.getCompanyEmail());
        company.setTax_code(request.getTax_code());
        company.setWebsite(request.getWebsite());
        company.setAddress(request.getAddress());
        company.setCity(request.getCity());
        company.setDescription(request.getDescription());
        company.setEmployeeCount(request.getEmployeeCount());
        company.setBusinessLicense(licenseUrl != null ? licenseUrl : company.getBusinessLicense());

        // Cập nhật lĩnh vực nếu có
        if (request.getJobCategoryId() != null) {
            JobCategory jobCategory = jobCategoryRepository.findById(request.getJobCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy lĩnh vực"));
            company.setJobCategory(jobCategory);
        }

        // Liên kết với user hiện tại
        company.setUser(currentUser);

        // Lưu và trả về company
        return companyRepository.save(company);
    }


}