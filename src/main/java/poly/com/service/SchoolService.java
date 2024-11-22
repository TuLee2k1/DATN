package poly.com.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import poly.com.dto.SchoolDto;
import poly.com.dto.response.SchoolResponse;
import poly.com.model.School;
import poly.com.repository.SchoolRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SchoolService {

    private final SchoolRepository schoolRepository;


    /*
     * @author: VuDD
     * @since: 10/17/2024 3:48 PM
     * @description:  Lấy danh sách tất cả trường học
     * @update:
     *
     * */
    public List<SchoolResponse> getAllSchools() {
       return schoolRepository.findAll().stream().map(this::convertToSchoolResponse).collect(Collectors.toList());
    }

    /*
     * @author: VuDD
     * @since: 10/17/2024 3:49 PM
     * @description:  Lấy thông tin trường học theo id
     * @update:
     *
     * */
    public SchoolResponse getSchoolById(Long id) {
        return schoolRepository.findById(id).map(this::convertToSchoolResponse).orElseThrow(() -> new RuntimeException("Không tim thấy trường học"));
    }

    /*
     * @author: VuDD
     * @since: 10/17/2024 3:49 PM
     * @description:  Luu trường học
     * @update:
     *
     * */
    public SchoolResponse saveSchool(SchoolDto schoolDto) {
        if (schoolDto.getStartDate().compareTo(schoolDto.getEndDate()) > 0) {
            throw new RuntimeException ("Ngày bắt đầu không thể lớn hơn ngày kết thúc");
        }
        try {
            School school = new School();
            school.setSchoolName(schoolDto.getSchoolName());
            school.setDegree(schoolDto.getDegree());
            school.setGPA(schoolDto.getGPA());
            school.setStartDate(schoolDto.getStartDate());
            school.setEndDate(schoolDto.getEndDate());
            return convertToSchoolResponse(schoolRepository.save(school));
        } catch (Exception e) {
            throw new RuntimeException("Lưu trường học thất bại");
        }
    }


    /*
     * @author: VuDD
     * @since: 10/17/2024 3:49 PM
     * @description:  Cập nhật trường học
     * @update:
     *
     * */
    public SchoolResponse updateSchool(Long id, SchoolDto schoolDto) {
        School school = schoolRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tim thấy trường học"));
        if (schoolDto.getStartDate().compareTo(schoolDto.getEndDate()) > 0) {
            throw new RuntimeException("Ngày bắt đầu không thể lớn hơn ngày kết thúc");
        }
        try {
            school.setSchoolName(schoolDto.getSchoolName());
            school.setDegree(schoolDto.getDegree());
            school.setGPA(schoolDto.getGPA());
            school.setStartDate(schoolDto.getStartDate());
            school.setEndDate(schoolDto.getEndDate());
            return convertToSchoolResponse(schoolRepository.save(school));
        } catch (Exception e) {
            throw new RuntimeException("Cập nhật trường học thất bại");
        }
    }


    /*
     * @author: VuDD
     * @since: 10/17/2024 3:50 PM
     * @description:  Xóa trường học
     * @update:
     *
     * */
    public void deleteSchool(Long id) {
        schoolRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tim thấy trường học"));
        try {
            schoolRepository.deleteById(id);
        } catch (Exception e) {
            throw new RuntimeException("Xóa trường học thất bại");
        }
    }

    private SchoolResponse convertToSchoolResponse(School school) {
        return SchoolResponse.builder()
                .id(school.getId())
                .schoolName(school.getSchoolName())
                .degree(school.getDegree())
                .startDate(school.getStartDate())
                .endDate(school.getEndDate())
                .GPA(school.getGPA())
                .profile_id(school.getProfile_id() != null ? school.getId() : null)
                .build();
    }

}
