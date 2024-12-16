package poly.com.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.com.model.JobPost;
import poly.com.repository.JobPostRepository;
import poly.com.util.AuthenticationUtil;
import poly.com.dto.response.Follow.CompanyFollowResponse;
import poly.com.dto.response.Follow.JobPostFollowResponse;
import poly.com.model.Follow;
import poly.com.repository.FollowRepository;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowService {

    private final FollowRepository followRepository;

    private AuthenticationUtil authenticationUtil;
    private final JobPostRepository jobPostRepository;


    public Follow toggleFollowCompany(Long companyId) {
        var user = authenticationUtil.getAuthenticatedUser();

        // Kiểm tra xem người dùng đã theo dõi công ty này chưa
        Optional<Follow> existingFollow = followRepository.findByUserIdAndCompanyId(user.getId(), companyId);

        if (existingFollow.isPresent()) {
            // Nếu đã theo dõi, xóa mối quan hệ
            followRepository.delete(existingFollow.get());
            return null; // Trả về null để biểu thị đã bỏ theo dõi
        }

        // Nếu chưa theo dõi, tạo mối quan hệ mới
        Follow follow = Follow.builder()
         .userId(user.getId())
         .companyId(companyId)
         .followDate(new Date())
         .build();

        return followRepository.save(follow); // Trả về đối tượng mới tạo
    }

    public Follow toggleFollowJobPost(Long jobPostId) {
        var user = authenticationUtil.getAuthenticatedUser();

        // Kiểm tra xem người dùng đã theo dõi bài đăng này chưa
        Optional<Follow> existingFollow = followRepository.findByUserIdAndJobPostId(user.getId(), jobPostId);

        if (existingFollow.isPresent()) {
            // Nếu đã theo dõi, xóa mối quan hệ
            followRepository.delete(existingFollow.get());
            return null;
        }

        // Nếu chưa theo dõi, tạo mối quan hệ mới
        Follow follow = Follow.builder()
         .userId(user.getId())
         .jobPostId(jobPostId)
         .followDate(new Date())
         .build();

        return followRepository.save(follow);
    }

    public Follow toggleFollowUser(Long userId) {
        var companyId = authenticationUtil.getAuthenticatedUser().getCompany().getId();

        // Check if the company is already following the user
        Optional<Follow> existingFollow = followRepository.findByCompanyIdAndUserId(companyId, userId);

        if (existingFollow.isPresent()) {
            // If already following, delete the relationship
            followRepository.delete(existingFollow.get());
            return null; // Return null to indicate unfollow
        }

        // If not following, create a new follow relationship
        Follow follow = Follow.builder()
         .companyId(companyId)
         .userId(userId)
         .followDate(new Date())
         .build();

        return followRepository.save(follow); // Return the newly created follow object
    }
    @Transactional
    public boolean toggleFollow(Long userId, Long jobPostId) {
        // Tìm kiếm xem đã follow chưa
        return followRepository.findByUserIdAndJobPostId(userId, jobPostId)
                .map(existingFollow -> {
                    // Nếu đã tồn tại thì xóa follow
                    followRepository.delete(existingFollow);
                    return false;
                })
                .orElseGet(() -> {
                    // Nếu chưa follow thì tạo mới
                    JobPost jobPost = jobPostRepository.findById(jobPostId)
                            .orElseThrow(() -> new RuntimeException("Job post not found"));

                    Follow newFollow = Follow.builder()
                            .userId(userId)
                            .jobPostId(jobPostId)
                            .companyId(jobPost.getCompany().getId())
                            .followDate(new Date())
                            .build();

                    followRepository.save(newFollow);
                    return true;
                });
    }

    public void unfollow(Long followId) {
        Optional<Follow> follow = followRepository.findById(followId);
        if (follow.isEmpty()) {
            throw new RuntimeException("Follow relationship not found.");
        }
        followRepository.deleteById(followId);
    }

    public void unfollowCompany(Long userId, Long companyId) {
        Optional<Follow> follow = followRepository.findByUserIdAndCompanyId(userId, companyId);
        if (follow.isEmpty()) {
            throw new RuntimeException("Follow relationship not found.");
        }
        followRepository.delete(follow.get());
    }

    public void unfollowJobPost(Long userId, Long jobPostId) {
        Optional<Follow> follow = followRepository.findByUserIdAndJobPostId(userId, jobPostId);
        if (follow.isEmpty()) {
            throw new RuntimeException("Follow relationship not found.");
        }
        followRepository.delete(follow.get());
    }

    public void unfollowApplicant(Long companyId, Long userId) {
        Optional<Follow> follow = followRepository.findByCompanyIdAndUserId(companyId, userId);
        if (follow.isEmpty()) {
            throw new RuntimeException("Follow relationship not found.");
        }
        followRepository.delete(follow.get());
    }

    /*
     * @author: VuDD
     * @since: 11/18/2024 3:48 PM
     * @description:  view danh sách các bài đăng mà người dùng theo dõi
     * @update:
     *
     * */
    public Page<JobPostFollowResponse> findUserFollowedJobPosts(Pageable pageable) {
        var userId = authenticationUtil.getAuthenticatedUser().getId();
        return followRepository.findUserFollowedJobPosts(userId, pageable);
    }

    public Page<CompanyFollowResponse> getFollowedCompanies(Pageable pageable) {
        var userId = authenticationUtil.getAuthenticatedUser().getId();
        return followRepository.findCompaniesFollowedByUser(userId, pageable);
    }

    public boolean isCompanyFollowingCandidate(Long companyId, Long userId) {
        return followRepository.existsByCompanyIdAndUserId(companyId, userId);
    }

    public boolean isFollowing(Long userId) {
        System.out.println("userId: " + userId);
        var companyId = authenticationUtil.getAuthenticatedUser().getCompany().getId();
        System.out.println("companyId: " + companyId);

        // Kiểm tra xem đã theo dõi chưa
        return followRepository.existsByCompanyIdAndUserId(companyId, userId);
    }

}