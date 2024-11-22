package poly.com.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import poly.com.dto.StaffDto;
import poly.com.dto.response.StaffResponse;
import poly.com.exception.ApiResponse;
import poly.com.service.StaffService;

import java.util.stream.Collectors;

@RestController
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;

    @GetMapping
    public ApiResponse<Page<StaffResponse>> getAllStaff(
    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        // Page<StaffResponse> staffPage = this.staffService.getAllStaff(pageNo);
        return ApiResponse.<Page<StaffResponse>>builder().Result(staffService.getAllStaff(pageNo)).build();
    }

    @GetMapping("/{id}")
    public ApiResponse<StaffResponse> getStaffById(@PathVariable Long id) {

        try {
            return ApiResponse.<StaffResponse>builder().status(HttpStatus.OK.value()).Result(staffService.getStaffById(id)).build();

        } catch (Exception e) {
            return ApiResponse.<StaffResponse>builder().status(HttpStatus.OK.value()).message("Không tìm thấy id: "+id).build();
        }
    }

    @GetMapping("/search")
    public ApiResponse<Page<StaffResponse>> searchStaff(
    @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword,
    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        return ApiResponse.<Page<StaffResponse>>builder().status(HttpStatus.OK.value()).Result(staffService.searchStaff(keyword
        , pageNo)).message("Tim tên nhân viên "+keyword).build();
    }

    @PostMapping("/save")
    public ApiResponse<StaffResponse> saveStaff(@Valid @RequestBody StaffDto staffDto, BindingResult result) {
        // Kiểm tra lỗi xác thực đầu vào
        if (result.hasErrors()) {
            String errors = result.getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .collect(Collectors.joining(", "));
            return ApiResponse.<StaffResponse>builder().status(HttpStatus.BAD_REQUEST.value()).message("Validation failed: "
            + errors).build();
        }

        try {
            // Lưu nhân viên nếu không có lỗi xác thực
            return ApiResponse.<StaffResponse>builder().status(HttpStatus.OK.value()).status(HttpStatus.CREATED.value())
            .Result(staffService.saveStaff(staffDto)).message("Tạo nhân viên thành công").build();
        } catch (DataIntegrityViolationException e) {
            return ApiResponse.<StaffResponse>builder().status(HttpStatus.CONFLICT.value()).message("Data integrity violation: "
            + e.getMessage()).build();
        } catch (Exception e) {
            return ApiResponse.<StaffResponse>builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("An error occurred: "
            + e.getMessage()).build();
        }
    }

    @PatchMapping("/{id}")
    public ApiResponse<StaffResponse> updateStaff(@PathVariable Long id, @Valid @RequestBody StaffDto staffDto) {
        try {
            return ApiResponse.<StaffResponse>builder().status(HttpStatus.OK.value()).Result(staffService.updateStaff(id, staffDto)).build();
        } catch (DataIntegrityViolationException e) {
            return ApiResponse.<StaffResponse>builder().status(HttpStatus.CONFLICT.value()).message("Data integrity violation: " + e.getMessage()).build();
        } catch (Exception e) {
            return ApiResponse.<StaffResponse>builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("An error occurred: " + e.getMessage()).build();
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deleteStaff(@PathVariable Long id) {
        try {
            staffService.deleteStaff(id);
            return ApiResponse.<String>builder().status(HttpStatus.OK.value()).message("Staff deleted successfully").build();
        } catch (Exception e) {
            return ApiResponse.<String>builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("An error occurred: " + e.getMessage()).build();
        }
    }
}
