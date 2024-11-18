package poly.com.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;


@Getter
@Setter
public class PageRequestDTO {
    private int pageNo = 0;
    private int size = 10;

    public Pageable getPageable(PageRequestDTO pageRequest) {
        int page = pageRequest.getPageNo();
        int size = pageRequest.getSize();

        PageRequest pageRequest1 = PageRequest.of(page, size);

        return PageRequest.of(pageNo, size);
    }
}
