package poly.com.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import poly.com.dto.request.AppliedJobRequest;
import poly.com.service.AppliedJobService;
import poly.com.service.FollowService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        int pageSize = 5;

        // Lấy công việc đã ứng tuyển
        List<AppliedJobRequest> allAppliedJobs = appliedJobService.getAppliedJobsByUserId(userId);

        // Tính toán phân trang cho applied jobs
        int totalAppliedItems = allAppliedJobs.size();
        int totalAppliedPages = (int) Math.ceil((double) totalAppliedItems / pageSize);

        int startIndex = (page - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalAppliedItems);
        List<AppliedJobRequest> appliedJobs = allAppliedJobs.subList(startIndex, endIndex);

        // Lấy danh sách gợi ý
        List<AppliedJobRequest> suggestedJobs = appliedJobService.getSuggestedJobs();


        // Thêm các biến vào Model
        model.addAttribute("appliedJobs", appliedJobs);
        model.addAttribute("suggestedJobs", suggestedJobs);
        model.addAttribute("totalPages", totalAppliedPages);
        model.addAttribute("currentPage", page);

        return "User/V3/apply";
    }



}