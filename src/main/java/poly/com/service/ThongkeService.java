package poly.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    public Map<String, Long> getStatistics() {
        Map<String, Long> stats = new HashMap<>();

        // Thống kê số lượng Company, JobPost, và Profile
        stats.put("totalCompanies", companyRepository.count());
        stats.put("totalJobPosts", jobPostRepository.count());
        stats.put("totalJobProfiles", profileRepository.count());
        stats.put("totalUser", userRepository.count());
        return stats;
    }

}
