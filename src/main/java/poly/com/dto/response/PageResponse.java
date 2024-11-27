
package poly.com.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.domain.Page;
import poly.com.dto.response.JobPost.JobListingResponse;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PageResponse<T> {
    private List<T> content;
    private long totalElements;
    private int totalPages;
    private int pageNumber;
    private int pageSize;

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.totalElements = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
    }

    public boolean hasNextPage() {
        return this.pageNumber < this.totalPages - 1; // Kiểm tra xem còn trang tiếp theo không
    }

    public boolean hasPreviousPage() {
        return this.pageNumber > 0; // Kiểm tra xem còn trang trước không
    }

    public int getNextPageNumber() {
        return this.pageNumber + 1; // Trả về số trang tiếp theo
    }

    public int getPreviousPageNumber() {
        return this.pageNumber - 1; // Trả về số trang trước
    }
}
