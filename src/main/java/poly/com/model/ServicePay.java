package poly.com.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name= "ServicePay")
public class ServicePay extends AbstractEntity{

    @Column(name = "serviceTitle") // Tiêu đề dịch vụ
    private String serviceTitle;

    @Column(name = "serviceDesc") // Mô tả dịch vụ
    private String serviceDesc;

    @Column(name = "servicePrice") // Giá dịch vụ
    private BigDecimal servicePrice;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "createdAt", updatable = false) // Ngày tạo
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "updatedAt") // Ngày cập nhật
    private LocalDateTime updatedAt;

    @Column(name = "serviceDuration") // Thời gian dịch vụ
    private Integer serviceDuration;

    @JsonFormat(pattern = "dd-MM-yyyy")
    @Column(name = "expiryDate") // Ngày hết hạn
    private LocalDateTime expiryDate;

    @Column(name = "serviceType") // Loại dịch vụ
    private String serviceType;

    @Column(name = "serviceImage") // Ảnh dịch vụ
    private String serviceImage;

    @Column(name = "serviceStatus") // Trạng thái dịch vụ
    private Boolean serviceStatus = false; // Mặc định là false -> false là chưa kích hoạt, true là đã kích hoạt

    @ManyToOne
    @JoinColumn(name = "pay_id")
    private Pay pay;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

}
