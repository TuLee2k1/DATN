package poly.com.util;

import java.util.ArrayList;
import java.util.List;

public class PaginationUtils {

    /**
     * Sinh danh sách số trang cho frontend pagination.
     *
     * @param currentPage   Trang hiện tại (bắt đầu từ 0).
     * @param totalPages    Tổng số trang.
     * @param maxDisplay    Số lượng nút hiển thị tối đa (bao gồm "..." nếu cần).
     * @return Danh sách số trang (bao gồm "null" đại diện cho "...").
     */
    public static List<Integer> getPaginationNumbers(int currentPage, int totalPages, int maxDisplay) {
        List<Integer> pages = new ArrayList<>();

        if (totalPages <= maxDisplay) {
            // Nếu số trang ít hơn hoặc bằng maxDisplay, hiển thị tất cả
            for (int i = 1; i <= totalPages; i++) {
                pages.add(i);
            }
        } else {
            int halfDisplay = maxDisplay / 2;

            // Hiển thị trang đầu tiên
            pages.add(1);

            // Nếu cần thêm "..." trước các trang ở giữa
            if (currentPage > halfDisplay) {
                pages.add(null); // Null đại diện cho "..."
            }

            // Hiển thị các trang ở giữa
            int start = Math.max(2, currentPage - halfDisplay);
            int end = Math.min(totalPages - 1, currentPage + halfDisplay);
            for (int i = start; i <= end; i++) {
                pages.add(i);
            }

            // Nếu cần thêm "..." sau các trang ở giữa
            if (currentPage + halfDisplay < totalPages - 1) {
                pages.add(null); // Null đại diện cho "..."
            }

            // Hiển thị trang cuối cùng
            pages.add(totalPages);
        }

        return pages;
    }
}
//@GetMapping
//public String getFollowedCompanies(
// @RequestParam(defaultValue = "0") int page,
// @RequestParam(defaultValue = "10") int size,
// Model model
//) {
//    Pageable pageable = PageRequest.of(page, size);
//    PaginationResponse<CompanyFollowResponse> paginationResponse = followService.getFollowedCompanies(pageable);
//
//    int totalPages = paginationResponse.getTotalPages();
//    int currentPage = paginationResponse.getCurrentPage();
//
//    // Sử dụng PaginationUtils để tạo danh sách số trang hiển thị
//    List<Integer> paginationNumbers = PaginationUtils.getPaginationNumbers(currentPage, totalPages, 7); // 7 là số nút tối đa hiển thị
//
//    model.addAttribute("companies", paginationResponse.getContent());
//    model.addAttribute("currentPage", currentPage);
//    model.addAttribute("totalPages", totalPages);
//    model.addAttribute("paginationNumbers", paginationNumbers);
//
//    return "followed-companies"; // Tên template Thymeleaf
//}

//
//        <!-- Hiển thị phân trang -->
//        <div>
//    <ul class="pagination">
//        <!-- Nút "Trang trước" -->
//        <li>
//            <a th:href="@{/followed-companies(page=${currentPage - 1})}"
//        th:classappend="${currentPage == 0} ? 'disabled'"
//        th:if="${currentPage > 0}">&laquo; Trước</a>
//        </li>
//
//        <!-- Hiển thị các số trang -->
//        <li th:each="pageNum : ${paginationNumbers}">
//            <a th:if="${pageNum != null}"
//        th:href="@{/followed-companies(page=${pageNum - 1})}"
//        th:classappend="${pageNum == currentPage + 1} ? 'active'"
//        th:text="${pageNum}"></a>
//            <span th:if="${pageNum == null}">...</span>
//        </li>
//
//        <!-- Nút "Trang sau" -->
//        <li>
//            <a th:href="@{/followed-companies(page=${currentPage + 1})}"
//        th:classappend="${currentPage + 1 == totalPages} ? 'disabled'"
//        th:if="${currentPage + 1 < totalPages}">Tiếp &raquo;</a>
//        </li>
//    </ul>
//</div>