package poly.com.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Value;
import poly.com.model.Company;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link poly.com.model.Pay}
 */
@Data
public class PayDto implements Serializable {
    private Long id;
    private Float amount;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date payDate;
    private String paymentType;
}