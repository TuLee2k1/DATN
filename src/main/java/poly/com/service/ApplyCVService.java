package poly.com.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import poly.com.Enum.StatusEnum;
import poly.com.util.AuthenticationUtil;
import poly.com.dto.request.ApplyCVRequest;
import poly.com.model.JobPost;
import poly.com.model.JobProfile;
import poly.com.repository.JobPostRepository;
import poly.com.repository.JobProfileRepository;

@Service
@RequiredArgsConstructor
public class ApplyCVService {

    private final JobPostRepository jobPostRepository;
    private final JobProfileRepository jobProfileRepository;
    private final FileStorageService fileStorageService;
    private final AuthenticationUtil authenticationUtil;


    @Transactional
    public JobProfile submitCv(MultipartFile multipartFile ,ApplyCVRequest request, Long jobPostId) {
        var jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new RuntimeException("Job post not found"));
        System.out.println("hello: "+jobPost.getAppliedCount());
        var user = authenticationUtil.getCurrentUser();


        // Lưu file CV
        String cvFileUrl = fileStorageService.storeImageProfileFile(multipartFile);
        System.out.println("CV File Url: " + cvFileUrl);

        // Chuyển đổi DTO sang Entity
        JobProfile cvSubmission = JobProfile.builder()
                .fullName(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhone())
                .fileCV(cvFileUrl)
                .jobPost(jobPost)
                .user(user)
                .status(StatusEnum.PENDING)
                .build();
        // Lưu vào database
        System.out.println("CV Submission: " + cvSubmission.getFileCV());
        System.out.println("CV Submission: " + cvSubmission.getEmail());
        System.out.println("CV Submission: " + cvSubmission.getJobPost());
        System.out.println("CV Submission: " + cvSubmission.getPhoneNumber());
        try {
            JobProfile savedSubmission = jobProfileRepository.save(cvSubmission);
            System.out.println("Saved submission: " + savedSubmission.getEmail());
            jobPost.setAppliedCount(jobPost.getAppliedCount() + 1);
            var savedJobPost = jobPostRepository.save(jobPost);
            System.out.println("Saved job post: " + savedJobPost.getAppliedCount());
            return savedSubmission;
        } catch (Exception e) {
            System.err.println("Error saving CV Submission: " + e.getMessage());
            e.printStackTrace(); // In ra stack trace để giúp bạn xác định vấn đề
        }

        return cvSubmission;
    }
}
