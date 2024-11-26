package poly.com.dto.response.JobPost;

import lombok.*;
import poly.com.Enum.StatusEnum;
import poly.com.model.JobPostStatus;

import java.util.Date;

@Getter
@Setter
@Builder
public class  JobListingResponse { // những class đuôi response là đẩy dữ liệu lên view
    private Long id;
    private String jobTitle;
    private Date createDate;
    private Date endDate;
    private Integer appliedCount;
    private JobPostStatus status; // Hhiển thị view lên
    private StatusEnum statusEnum; /// chờ ađ duyệt

}