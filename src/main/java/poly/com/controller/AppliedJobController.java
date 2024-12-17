package poly.com.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import poly.com.dto.request.AppliedJobRequest;
import poly.com.service.AppliedJobService;
import poly.com.service.FollowService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class AppliedJobController {

    private final AppliedJobService appliedJobService;
    private final FollowService followService;
    private static final int PAGE_SIZE = 5; // Số lượng item trên mỗi trang

    @GetMapping("/user-applied")
    public String getAppliedJobs(Model model, HttpSession session, @RequestParam(defaultValue = "1") int page) {
        Long userId = (Long) session.getAttribute("userId");
        if (userId == null) {
            return "redirect:/auth/login";
        }

        // Lấy công việc đã ứng tuyển
        List<AppliedJobRequest> allAppliedJobs = appliedJobService.getAppliedJobsByUserId(userId);

        // Tính toán phân trang cho applied jobs
        int totalAppliedItems = allAppliedJobs.size();
        int totalAppliedPages = (int) Math.ceil((double) totalAppliedItems / PAGE_SIZE);

        int startIndex = (page - 1) * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, totalAppliedItems);
        List<AppliedJobRequest> appliedJobs = allAppliedJobs.subList(startIndex, endIndex);

        // Lấy danh sách gợi ý công việc và trộn ngẫu nhiên
        List<AppliedJobRequest> suggestedJobs = appliedJobService.getSuggestedJobs();
        Collections.shuffle(suggestedJobs); // Trộn danh sách
        List<AppliedJobRequest> randomSuggestedJobs = suggestedJobs.stream()
                .limit(4) // Lấy 4 job ngẫu nhiên
                .collect(Collectors.toList());

        // Thêm các biến vào Model
        model.addAttribute("appliedJobs", appliedJobs);
        model.addAttribute("suggestedJobs", randomSuggestedJobs); // Danh sách ngẫu nhiên
        model.addAttribute("totalPages", totalAppliedPages);
        model.addAttribute("currentPage", page);

        return "User/V3/apply";
    }
}
