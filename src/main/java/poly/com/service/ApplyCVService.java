package poly.com.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import poly.com.Enum.StatusEnum;
import poly.com.util.AuthenticationUtil;
import poly.com.dto.request.ApplyCVRequest;
import poly.com.exception.FileStorageException;
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
    public JobProfile submitCv(MultipartFile multipartFile, ApplyCVRequest request, Long jobPostId) {
        // 1. Lấy thông tin tin tuyển dụng
        JobPost jobPost = jobPostRepository.findById(jobPostId)
         .orElseThrow(() -> new RuntimeException("Job post not found with ID: " + jobPostId));
        System.out.println("Job Post Applied Count: " + jobPost.getAppliedCount());

        // 2. Lấy thông tin người dùng hiện tại
        var user = authenticationUtil.getCurrentUser();

        // 3. Lưu file CV
        String cvFileName;
        try {
            cvFileName = fileStorageService.storeFile(multipartFile);
            System.out.println("CV File uploaded successfully: " + cvFileName);
        } catch (FileStorageException ex) {
            throw new RuntimeException("Failed to upload CV file.", ex);
        }

        // 4. Chuyển đổi DTO sang Entity và lưu vào database
        JobProfile jobProfile = JobProfile.builder()
         .fullName(request.getName())
         .email(request.getEmail())
         .phoneNumber(request.getPhone())
         .fileCV(cvFileName) // Đường dẫn file CV
         .jobPost(jobPost)
         .status(StatusEnum.PENDING)
         .user(user)
         .build();

        try {
            // Lưu hồ sơ ứng tuyển
            JobProfile savedProfile = jobProfileRepository.save(jobProfile);

            // Tăng appliedCount và lưu lại JobPost
            jobPost.setAppliedCount(jobPost.getAppliedCount() + 1);
            jobPostRepository.save(jobPost);

            System.out.println("Saved CV Submission for: " + savedProfile.getEmail());
            return savedProfile;

        } catch (Exception e) {
            System.err.println("Error saving CV Submission: " + e.getMessage());
            throw new RuntimeException("Error saving CV submission, please try again.", e);
        }
    }
}
