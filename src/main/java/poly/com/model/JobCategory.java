package poly.com.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Table(name= "JobCategory")
public class JobCategory extends AbstractEntity {
    @Column(name = "categoryName") // danh mục cha
    private String categoryName;

    @Column(name = "description") // mô tả
    private String description;

    @Column(name = "status") // trạng thái
    private Boolean status = true; // true: active, false: deactive

    @OneToMany(mappedBy = "jobCategory") // Sửa đổi ở đây
    private List<SubCategory> subCategories; // Danh sách danh mục con
}
