package poly.com.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
public class SubCategory extends AbstractEntity{

    private String subCategoryName; // Tên danh mục con
    private String description; // Mô tả
    private Boolean status = true; // Trạng thái

    @ManyToOne
    @JoinColumn(name = "job_category_id") // Khóa ngoại liên kết đến JobCategory
    private JobCategory jobCategory; // Danh mục cha
}
