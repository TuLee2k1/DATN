package poly.com.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import poly.com.dto.StaffDto;
import poly.com.dto.response.StaffResponse;
import poly.com.model.Staff;
import poly.com.repository.StaffRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StaffService {

    private final StaffRepository staffRepository;


    public List<StaffResponse> getAllStaffs() {
        return staffRepository.findAll().stream().map(this::convertToStaffResponse).collect(Collectors.toList());
    }


    public StaffResponse getStaffById(Long id) {
        Staff staff = staffRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tim thấy nhân viên"));
        return convertToStaffResponse(staff);
    }

    @Transactional
    public StaffResponse saveStaff(StaffDto staffDto) {
        if (staffRepository.existsByFullName(staffDto.getFullName())) {
            throw new RuntimeException("Staff already exists");
        }
        Staff staff = new Staff();
        staff.setFullName(staffDto.getFullName());
        staff.setPhoneNumber(staffDto.getPhoneNumber());
        staff.setPosition(staffDto.getPosition());
        staff.setAddress(staffDto.getAddress());
        staff.setDob(staffDto.getDob());
        return convertToStaffResponse(staffRepository.save(staff));
    }

    @Transactional
    public StaffResponse updateStaff(Long id, StaffDto staffDto) {
        Staff staff = staffRepository.findById(id).orElseThrow(() -> new RuntimeException("Không tim thấy nhân viên"));
        staff.setFullName(staffDto.getFullName());
        staff.setPhoneNumber(staffDto.getPhoneNumber());
        staff.setPosition(staffDto.getPosition());
        staff.setAddress(staffDto.getAddress());
        staff.setDob(staffDto.getDob());
        return convertToStaffResponse(staffRepository.save(staff));
    }

    public void deleteStaff(Long id) {
        staffRepository.deleteById(id);
    }


    public Page<StaffResponse> getAllStaff(Integer pageNo) {
        // Tạo đối tượng sắp xếp theo tên tăng dần
        Sort sort = Sort.by(Sort.Direction.ASC, "fullName");

        // Tạo đối tượng phân trang với sắp xếp
        Pageable pageable = PageRequest.of(pageNo - 1, 10, sort);

        Page<Staff> staffs = staffRepository.findAll(pageable);

        return staffs.map(this::convertToStaffResponse);
    }

    public Page<StaffResponse> searchStaff(String keyword, Integer pageNo) {
        Sort sort = Sort.by(Sort.Direction.ASC, "fullName");
        Pageable pageable = PageRequest.of(pageNo - 1, 10, sort);
        Page<Staff> staffs = staffRepository.findByFullNameContaining(keyword, pageable);
        return staffs.map(this::convertToStaffResponse);
    }

    private StaffResponse convertToStaffResponse(Staff staff) {
        return StaffResponse.builder()
        .id(staff.getId())
        .fullName(staff.getFullName())
        .phoneNumber(staff.getPhoneNumber())
        .position(staff.getPosition())
        .address(staff.getAddress())
        .dob(staff.getDob())
        .user_id(staff.getUser() != null ? staff.getUser().getId() : null) // Xử lý khóa ngoại user
        .build();
    }
}
