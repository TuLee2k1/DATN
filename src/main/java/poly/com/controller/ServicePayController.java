package poly.com.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import poly.com.dto.ServicePayDto;
import poly.com.dto.response.ServicePayResponse;
import poly.com.exception.ApiResponse;
import poly.com.service.ServicePayService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/service-pay")
@RequiredArgsConstructor
public class ServicePayController {

    private final ServicePayService servicePayService;

    @GetMapping
    public ApiResponse<List<ServicePayResponse>> getAllServicePays() {
        List<ServicePayResponse> servicePays = servicePayService.getAllServicePays();
        return ApiResponse.<List<ServicePayResponse>>builder().Result(servicePays).build();
    }

    @GetMapping("/{id}")
    public ApiResponse<ServicePayResponse> getServicePayById(@PathVariable Long id) {
        try {
            ServicePayResponse servicePay = servicePayService.getServicePayById(id);
            return ApiResponse.<ServicePayResponse>builder().status(HttpStatus.OK.value()).Result(servicePay).message("Tìm thấy dịch vụ với id: "+id).build();
        } catch (Exception e) {
            return ApiResponse.<ServicePayResponse>builder().status(HttpStatus.OK.value()).message("Không tìm thấy id: " + id).build();
        }
    }

    @PostMapping("/save")
    public ApiResponse<ServicePayResponse> saveServicePay(@Valid @RequestBody ServicePayDto servicePayDto, BindingResult result) {
        if (result.hasErrors()) {
            String errors = result.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                    .collect(Collectors.joining(", "));

            return ApiResponse.<ServicePayResponse>builder().status(HttpStatus.BAD_REQUEST.value()).message("Validation failed: " + errors).build();
        }
        try {
            ServicePayResponse servicePay = servicePayService.saveServicePay(servicePayDto);
            return ApiResponse.<ServicePayResponse>builder().status(HttpStatus.CREATED.value()).Result(servicePay).message("Lưu dịch vụ thành công").build();
        } catch (Exception e) {
            return ApiResponse.<ServicePayResponse>builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("Lưu dịch vụ thất bại").build();
        }
    }

    @PatchMapping("/{id}")
    public ApiResponse<ServicePayResponse> updateServicePay(@PathVariable Long id,
                                                            @Valid @RequestBody ServicePayDto servicePayDto,
                                                            BindingResult result) {
        if (result.hasErrors()) {
            String errors = result.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                    .reduce("", (s1, s2) -> s1 + ", " + s2);
            return ApiResponse.<ServicePayResponse>builder().status(HttpStatus.BAD_REQUEST.value()).message("Validation failed: " + errors).build();
        }
        try {
            ServicePayResponse servicePay = servicePayService.updateServicePay(id, servicePayDto);
            return ApiResponse.<ServicePayResponse>builder().status(HttpStatus.OK.value()).Result(servicePay).message("Cập nhật dịch vụ thành công").build();
        } catch (Exception e) {
            return ApiResponse.<ServicePayResponse>builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("Cập nhật dịch vụ thất bại").build();
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteServicePay(@PathVariable Long id) {
        try {
            servicePayService.deleteServicePay(id);
            return ApiResponse.<Void>builder().status(HttpStatus.OK.value()).message("Xóa dịch vụ thành công").build();
        } catch (Exception e) {
            return ApiResponse.<Void>builder().status(HttpStatus.INTERNAL_SERVER_ERROR.value()).message("Xóa dịch vụ thất bại").build();
        }
    }
}
