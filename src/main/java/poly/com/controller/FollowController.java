//package poly.com.controller;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import poly.com.dto.response.Follow.CompanyFollowResponse;
//import poly.com.dto.response.Follow.JobPostFollowResponse;
//import poly.com.model.Follow;
//import poly.com.service.FollowService;
//
//@RestController
//@RequestMapping("/api/follow")
//@RequiredArgsConstructor
//public class FollowController {
//
//    private final FollowService followService;
//
//    // Theo dõi công ty
//    @PostMapping("/company/toggle")
//    public ResponseEntity<String> toggleFollowCompany(@RequestParam Long companyId) {
//        Follow result = followService.toggleFollowCompany(companyId);
//        if (result == null) {
//            return ResponseEntity.ok("Đã bỏ theo dõi công ty.");
//        }
//        return ResponseEntity.ok("Đã theo dõi công ty.");
//    }
//
//    // Theo dõi bài đăng tuyển dụng
//    @PostMapping("/job-post/toggle")
//    public ResponseEntity<String> toggleFollowJobPost(@RequestParam Long jobPostId) {
//        Follow result = followService.toggleFollowJobPost(jobPostId);
//        if (result == null) {
//            return ResponseEntity.ok("Đã bỏ theo dõi bài đăng tuyển dụng.");
//        }
//        return ResponseEntity.ok("Đã theo dõi bài đăng tuyển dụng.");
//    }
//
//    // Theo dõi ứng viên
//    @PostMapping("/applicant/toggle")
//    public ResponseEntity<String> toggleFollowApplicant(@RequestParam Long userId) {
//        Follow result = followService.toggleFollowApplicant(userId);
//        if (result == null) {
//            return ResponseEntity.ok("Đã bỏ theo dõi ứng viên.");
//        }
//        return ResponseEntity.ok("Đã theo dõi ứng viên.");
//    }
//
//    // Bỏ theo dõi bất kỳ
//    @DeleteMapping("/{followId}")
//    public ResponseEntity<Void> unfollow(@PathVariable Long followId) {
//        followService.unfollow(followId);
//        return ResponseEntity.noContent().build();
//    }
//
//
//
//    // Lấy danh sách bài đăng tuyển dụng mà người dùng đã theo dõi
//    @GetMapping("/job-posts")
//    public ResponseEntity<Page<JobPostFollowResponse>> getFollowedJobPosts(
//     @RequestParam(defaultValue = "0") int page,  // Trang hiện tại (mặc định là 0)
//     @RequestParam(defaultValue = "10") int size // Kích thước mỗi trang (mặc định là 10)
//    ) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<JobPostFollowResponse> followedJobPosts = followService.findUserFollowedJobPosts(pageable);
//
//        return ResponseEntity.ok(followedJobPosts); // Trả về Page<JobPostFollowResponse>
//    }
//
////    @GetMapping("/job-posts")
////    public String getFollowedJobPosts(
////     @RequestParam(defaultValue = "0") int page,
////     @RequestParam(defaultValue = "10") int size,
////     Model model
////    ) {
////        Pageable pageable = PageRequest.of(page, size);
////        Page<JobPostFollowResponse> followedJobPosts = followService.findUserFollowedJobPosts(pageable);
////
////        model.addAttribute("jobPosts", followedJobPosts.getContent());
////        model.addAttribute("currentPage", followedJobPosts.getNumber());
////        model.addAttribute("totalPages", followedJobPosts.getTotalPages());
////        model.addAttribute("paginationNumbers", PaginationUtils.getPaginationNumbers(followedJobPosts.getNumber(), followedJobPosts.getTotalPages(), 7));
////
////        return "job-posts"; // Tên template Thymeleaf
////    }
//
//    // Lấy danh sách công ty mà người dùng đã theo dõi
//    public ResponseEntity<Page<CompanyFollowResponse>> getFollowedCompanies(
//     @RequestParam(defaultValue = "0") int page,  // Trang hiện tại (mặc định là 0)
//     @RequestParam(defaultValue = "10") int size // Kích thước mỗi trang (mặc định là 10)
//    ) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<CompanyFollowResponse> followedCompanies = followService.getFollowedCompanies(pageable);
//
//        return ResponseEntity.ok(followedCompanies); // Trả về Page<CompanyFollowResponse>
//    }
//
////    @GetMapping("/companies")
////    public String getFollowedCompanies(
////     @RequestParam(defaultValue = "0") int page,
////     @RequestParam(defaultValue = "10") int size,
////     Model model
////    ) {
////        Pageable pageable = PageRequest.of(page, size);
////        Page<CompanyFollowResponse> followedCompanies = followService.getFollowedCompanies(pageable);
////
////        model.addAttribute("companies", followedCompanies.getContent());
////        model.addAttribute("currentPage", followedCompanies.getNumber());
////        model.addAttribute("totalPages", followedCompanies.getTotalPages());
////        model.addAttribute("paginationNumbers", PaginationUtils.getPaginationNumbers(followedCompanies.getNumber(), followedCompanies.getTotalPages(), 7));
////
////        return "companies"; // Tên template Thymeleaf
////    }
//
//}

package poly.com.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import poly.com.model.User;
import poly.com.service.FollowService;
import poly.com.util.AuthenticationUtil;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/follow")
public class FollowController {

    private final FollowService followService;
    private final AuthenticationUtil authenticationUtil;

    @PostMapping("/toggle-follow-job/{jobPostId}")
    public ResponseEntity<Map<String, String>> toggleFollowJob(HttpSession session, @PathVariable Long jobPostId) {
        try {
            // Kiểm tra người dùng đã đăng nhập hay chưa
            User currentUser = authenticationUtil.getCurrentUser();
            if (currentUser == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Vui lòng đăng nhập trước khi thực hiện thao tác này.");
                return ResponseEntity.status(401).body(response);
            }

            // Lưu trạng thái theo dõi
            followService.toggleFollowJobPost(jobPostId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Đã thêm vào danh sách theo dõi");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/unfollow-job/{jobPostId}")
    public ResponseEntity<Map<String, String>> unfollowJob(HttpSession session, @PathVariable Long jobPostId) {
        try {
            // Kiểm tra người dùng đã đăng nhập hay chưa
            User currentUser = authenticationUtil.getCurrentUser();
            if (currentUser == null) {
                Map<String, String> response = new HashMap<>();
                response.put("message", "Vui lòng đăng nhập trước khi thực hiện thao tác này.");
                return ResponseEntity.status(401).body(response);
            }

            // Xóa trạng thái theo dõi công việc
            followService.unfollowJobPost(jobPostId);

            Map<String, String> response = new HashMap<>();
            response.put("message", "Đã xóa khỏi danh sách theo dõi");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Có lỗi xảy ra: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }



}
