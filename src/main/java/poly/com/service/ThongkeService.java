package poly.com.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.com.repository.*;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ThongkeService {
    private final JobProfileRepository jobProfileRepository;
    private final FollowRepository followRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private JobPostRepository jobPostRepository;

    @Autowired
    private ProfileRepository profileRepository;

    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();

        // Thống kê số lượng Company, JobPost, và Profile
        stats.put("totalCompanies", companyRepository.count());
        stats.put("totalJobPosts", jobPostRepository.count());
        stats.put("totalJobProfiles", profileRepository.count());

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
