package poly.com.controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import poly.com.dto.SchoolDto;
import poly.com.dto.response.SchoolResponse;
import poly.com.exception.ApiResponse;
import poly.com.service.SchoolService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/school")
@RequiredArgsConstructor
public class SchoolController {

    private final SchoolService schoolService;

    @GetMapping
    public ApiResponse<List<SchoolResponse>> getAllSchool() {
        List<SchoolResponse> schools = schoolService.getAllSchools();
        return ApiResponse.<List<SchoolResponse>>builder().Result(schools).build();
    }

    @GetMapping("/{id}")
    public ApiResponse<SchoolResponse> getSchoolById(@PathVariable Long id) {
        try {
            SchoolResponse school = schoolService.getSchoolById(id);
            return ApiResponse.<SchoolResponse>builder().status(HttpStatus.OK.value()).Result(school).message("Tìm " +
            "thấy truờng với id: "+id).build();
        } catch (Exception e) {
            return ApiResponse.<SchoolResponse>builder().status(HttpStatus.OK.value()).message("Không tìm thấy id: " + id).build();
        }
    }

    @PostMapping("/save")
    public ApiResponse<SchoolResponse> saveSchool(@Valid @RequestBody SchoolDto schoolDto, BindingResult result) {
        if (result.hasErrors()) {
            String errors = result.getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .collect(Collectors.joining(", "));

            return ApiResponse.<SchoolResponse>builder().status(HttpStatus.BAD_REQUEST.value()).message("Validation failed: " + errors).build();
        }
        try {
            SchoolResponse school = schoolService.saveSchool(schoolDto);
            return ApiResponse.<SchoolResponse>builder().status(HttpStatus.CREATED.value()).Result(school).message("Lưu trường học thành công").build();
        } catch (Exception e) {
            return ApiResponse.<SchoolResponse>builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("Lưu trường học thất bại").build();
        }
    }

    @PatchMapping("/{id}")
    public ApiResponse<SchoolResponse> updateSchool(@PathVariable Long id, @Valid @RequestBody SchoolDto schoolDto,
                                                    BindingResult result) {
        if (result.hasErrors()) {
            String errors = result.getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .collect(Collectors.joining(", "));

            return ApiResponse.<SchoolResponse>builder().status(HttpStatus.BAD_REQUEST.value()).message("Validation failed: " + errors).build();
        }
        try {
            SchoolResponse school = schoolService.updateSchool(id, schoolDto);
            return ApiResponse.<SchoolResponse>builder().status(HttpStatus.OK.value()).Result(school).message("Cập nhật trường học thành công").build();
        } catch (Exception e) {
            return ApiResponse.<SchoolResponse>builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("Cập nhật trường học thất bại").build();
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<SchoolResponse> deleteSchool(@PathVariable Long id) {
        try {
            schoolService.deleteSchool(id);
            return ApiResponse.<SchoolResponse>builder().status(HttpStatus.OK.value()).message("Xóa trường học thành công").build();
        } catch (Exception e) {
            return ApiResponse.<SchoolResponse>builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("Xóa trường học thất bại").build();
        }
    }
}
