package poly.com.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import poly.com.dto.request.followsRequest;
import poly.com.dto.response.Follow.JobPostFollowResponse;
import poly.com.dto.response.SavedJobPostResponse;
import poly.com.model.User;
import poly.com.service.FollowService;
import poly.com.util.AuthenticationUtil;

import java.util.List;
@Controller
@RequestMapping("/user-saved")
@RequiredArgsConstructor
public class SavedJobController {

    private final FollowService followService;

    @GetMapping("")
    public String getSavedJobs(
            @RequestParam(defaultValue = "0") int page,  // Trang hiện tại (mặc định là 0)
            @RequestParam(defaultValue = "10") int size, // Số item mỗi trang (mặc định là 10)
            Model model) {
        Pageable pageable = PageRequest.of(page, size);
        Page<JobPostFollowResponse> savedJobs = followService.getSavedJobs(pageable);

        // Đẩy dữ liệu phân trang và danh sách công việc vào Model
        model.addAttribute("savedJobs", savedJobs.getContent()); // Danh sách công việc
        System.out.println("Saved Jobs: " + savedJobs.getContent().size());
        model.addAttribute("currentPage", page);                // Trang hiện tại
        model.addAttribute("totalPages", savedJobs.getTotalPages()); // Tổng số trang
        return "User/V3/saved_jobs";
    }
}

