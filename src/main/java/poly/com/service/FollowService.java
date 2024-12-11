package poly.com.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import poly.com.Util.AuthenticationUtil;
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


    public Follow toggleFollowApplicant(Long userId) {
        var companyId = authenticationUtil.getAuthenticatedUser().getCompany().getId();

        // Kiểm tra xem công ty đã theo dõi ứng viên chưa
        Optional<Follow> existingFollow = followRepository.findByCompanyIdAndUserId(companyId, userId);

        if (existingFollow.isPresent()) {
            // Nếu đã theo dõi, xóa mối quan hệ
            followRepository.delete(existingFollow.get());
            return null;
        }

        // Nếu chưa theo dõi, tạo mối quan hệ mới
        Follow follow = Follow.builder()
         .companyId(companyId)
         .userId(userId)
         .followDate(new Date())
         .build();

        return followRepository.save(follow);
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



}
