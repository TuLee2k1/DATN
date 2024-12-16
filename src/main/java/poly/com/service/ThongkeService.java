package poly.com.service;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.ModelMap;
import poly.com.dto.response.Auth.AuthenticationResponse;
import poly.com.repository.*;

import java.util.HashMap;
import java.util.Map;

@Service
public class ThongkeService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobPostRepository jobPostRepository;

    @Autowired
    private ProfileRepository profileRepository;
//--------------------------------------------

    @Autowired
    private FollowRepository followRepository;

    @Autowired
    private JobProfileRepository jobProfileRepository;


    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();


        // Thống kê dành cho admin
        stats.put("totalCompanies", companyRepository.count());
        stats.put("totalJobPosts", jobPostRepository.count());
        stats.put("totalJobProfiles", profileRepository.count());
        stats.put("totalUser", userRepository.count());

        // Thống kê dành cho users

        stats.put("bookmarked", followRepository.count());
        stats.put("appliedJobs", jobProfileRepository.count());

        return stats;
    }



    // Thêm phương thức thống kê công việc đã ứng tuyển của user theo userId
    public Long getAppliedJobsByUserId(Long userId) {
        return jobProfileRepository.countByUserId(userId);
    }
    public Long getBookmarkedByUserId(Long userId) {
        return followRepository.countBookmarksByUserId(userId);
    }
}
