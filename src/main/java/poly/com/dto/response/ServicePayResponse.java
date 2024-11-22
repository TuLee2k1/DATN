package poly.com.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ServicePayResponse {
    private Long id;  // ID của dịch vụ

     String serviceTitle; // Tiêu đề dịch vụ

     String serviceDesc; // Mô tả dịch vụ

     BigDecimal servicePrice; // Giá dịch vụ

     Integer serviceDuration; // Thời gian của dịch vụ (giờ hoặc ngày)

     String serviceType; // Loại dịch vụ (basic, premium, vip,...)

     String serviceImage; // Đường dẫn hoặc URL hình ảnh đại diện của dịch vụ

     Boolean serviceStatus; // Trạng thái hoạt động của dịch vụ (kích hoạt hoặc không)

     LocalDateTime createdAt; // Ngày tạo dịch vụ (nếu cần hiển thị)

     LocalDateTime updatedAt; // Ngày cập nhật dịch vụ (nếu cần hiển thị)

     Long pay_id; // ID của hóa đơn thanh toán
}
