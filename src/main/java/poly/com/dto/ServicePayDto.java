package poly.com.dto;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import poly.com.model.Pay;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class ServicePayDto implements Serializable {

    @NotNull(message = "Service title is required")
    @Size(min = 3, max = 100, message = "Service title must be between 3 and 100 characters")
    private String serviceTitle;

    @NotNull(message = "Service description is required")
    @Size(min = 10, max = 500, message = "Service description must be between 10 and 500 characters")
    private String serviceDesc;

    @NotNull(message = "Service price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Service price must be greater than 0")
    private BigDecimal servicePrice;

    @NotNull(message = "Service duration is required")
    @Min(value = 1, message = "Service duration must be at least 1 hour")
    @Max(value = 365, message = "Service duration must be less than or equal to 365 days")
    private Integer serviceDuration; // Duration in hours or days, depending on your business logic

    @NotNull(message = "Service type is required")
    @Pattern(regexp = "^(basic|premium)$", message = "Service type must be one of the following: basic, premium")
    private String serviceType;

    private String serviceImage;

    @NotNull(message = "Service status is required")
    private Boolean serviceStatus = false; //

}
