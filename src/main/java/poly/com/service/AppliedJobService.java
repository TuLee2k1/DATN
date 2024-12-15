package poly.com.service;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.com.Enum.StatusEnum;
import poly.com.dto.request.AppliedJobRequest;
import poly.com.model.Follow;
import poly.com.model.JobProfile;
import poly.com.model.JobPost;
import poly.com.repository.FollowRepository;
import poly.com.repository.JobPostRepository;
import poly.com.repository.JobProfileRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppliedJobService {
    private final FollowRepository followRepository;
    private final JobProfileRepository jobProfileRepository;
    private final JobPostRepository jobPostRepository;
    private final HttpSession session;

    private static final String LOGO_BASE_PATH = "/uploads/logocompany/";

    // Lấy danh sách công việc đã ứng tuyển
    public List<AppliedJobRequest> getAppliedJobsByUserId(Long userId) {
        List<JobProfile> jobProfiles = jobProfileRepository.findByUser_Id(userId);

        return jobProfiles.stream()
                .map(profile -> {
                    JobPost jobPost = profile.getJobPost();
                    String logoPath = jobPost.getCompany().getLogo() != null
                            ? LOGO_BASE_PATH + jobPost.getCompany().getLogo()
                            : null; // Xử lý nếu không có logo
                    return new AppliedJobRequest(
                            profile.getId(),
                            jobPost.getJobTitle(),
                            jobPost.getWorkType().toString(),
                            jobPost.getCompany().getName(),
                            jobPost.getMinSalary(),
                            jobPost.getMaxSalary(),
                            profile.getStatus().toString(),
                            logoPath,
                            jobPost.getAddress()
                    );
                })
                .collect(Collectors.toList());
    }
    // Lấy danh sách việc làm gợi ý
    public List<AppliedJobRequest> getSuggestedJobs() {
        return jobPostRepository.findByStatusEnum(StatusEnum.ACTIVE).stream()
                .map(jobPost -> {
                    String logoPath = jobPost.getCompany().getLogo() != null
                            ? LOGO_BASE_PATH + jobPost.getCompany().getLogo()
                            : null; // Xử lý nếu không có logo
                    return new AppliedJobRequest(
                            jobPost.getId(),
                            jobPost.getJobTitle(),
                            jobPost.getWorkType().toString(),
                            jobPost.getCompany().getName(),
                            jobPost.getMinSalary(),
                            jobPost.getMaxSalary(),
                            "ACTIVE",
                            logoPath,
                            jobPost.getAddress()
                    );
                })
                .collect(Collectors.toList());
    }


}
