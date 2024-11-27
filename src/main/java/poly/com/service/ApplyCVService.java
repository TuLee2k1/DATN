package poly.com.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
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


    @Transactional
    public JobProfile submitCv(MultipartFile multipartFile ,ApplyCVRequest request, Long jobPostId) {
        var jobPost = jobPostRepository.findById(jobPostId)
                .orElseThrow(() -> new RuntimeException("Job post not found"));


        // Lưu file CV
        String cvFileUrl = fileStorageService.storeImageProfileFile(multipartFile);

        // Chuyển đổi DTO sang Entity
        JobProfile cvSubmission = JobProfile.builder()
                .fullName(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhone())
                .fileCV(cvFileUrl)
                .jobPost(jobPost)
                .build();
        // Lưu vào database
        JobProfile savedSubmission = jobProfileRepository.save(cvSubmission);
        jobPost.setAppliedCount(jobPost.getAppliedCount() + 1);
        var savedJobPost = jobPostRepository.save(jobPost);

        return savedSubmission;
    }


}
