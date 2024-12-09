package poly.com.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import poly.com.Enum.StatusEnum;
import poly.com.dto.response.PageResponse;
import poly.com.model.Company;
import poly.com.model.JobProfile;
import poly.com.repository.JobProfileRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobProfileService {
    private final JobProfileRepository jobProfileRepository;


    public Page<JobProfile> getAllJobProfiles(Pageable pageable) {
        return jobProfileRepository.findAll(pageable);
    }

    public List<JobProfile> getAllJobProfiles() {
        return jobProfileRepository.findAll();
    }

    public Optional<JobProfile> getJobProfileById(Long id) {
        return jobProfileRepository.findById(id);
    }

    public JobProfile saveJobProfile(JobProfile jobProfile) {
        return jobProfileRepository.save(jobProfile);
    }

    public void deleteJobProfile(Long id) {
        jobProfileRepository.deleteById(id);
    }

    public PageResponse<JobProfile> getALlProfileByJobPostid(Long jobPostId,Integer pageNo){
       Pageable pageable = PageRequest.of(pageNo - 1, 10);
         Page<JobProfile> jobProfiles = jobProfileRepository.findJobProfilesByJobPostId(jobPostId, pageable);
            return new PageResponse<>(jobProfiles);
    }

    public PageResponse<JobProfile> getAllProfilesByCompany(Company company, Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        Page<JobProfile> jobProfiles = jobProfileRepository.findByJobPost_Company(company, pageable);
        return new PageResponse<>(jobProfiles);
    }

    /**
     * Đếm tổng số hồ sơ ứng tuyển cho một bài đăng việc làm
     *
     * @param jobPostId ID của bài đăng việc làm
     * @return Tổng số hồ sơ
     */
    public Long countTotalProfilesByJobPost(Long jobPostId) {
        try {
            if (jobPostId == null) {
                // Nếu jobPostId là null, đếm tất cả hồ sơ ứng tuyển
                return jobProfileRepository.count();
            } else {
                // Nếu jobPostId không null, đếm hồ sơ ứng tuyển theo jobPostId
                return jobProfileRepository.countByJobPostId(jobPostId);
            }
        } catch (Exception e) {
            log.error("Error counting total profiles for jobPostId: {}", jobPostId, e);
            throw new ServiceException("Không thể đếm hồ sơ ứng tuyển", e);
        }
    }

    /**
     * Đếm số hồ sơ ứng tuyển theo trạng thái cho một bài đăng việc làm
     *
     * @param jobPostId ID của bài đăng việc làm
     * @param status Trạng thái hồ sơ cần đếm
     * @return Số lượng hồ sơ theo trạng thái
     */
    public Long countProfilesByJobPostAndStatus(Long jobPostId, StatusEnum status) {
        try {
            if (jobPostId == null) {
                // Nếu jobPostId là null, đếm hồ sơ ứng tuyển theo trạng thái (tất cả bài đăng)
                return jobProfileRepository.countByStatus(status);
            } else {
                // Nếu jobPostId không phải null, đếm hồ sơ ứng tuyển theo bài đăng và trạng thái
                return jobProfileRepository.countByJobPostIdAndStatus(jobPostId, status);
            }
        } catch (Exception e) {
            log.error("Error counting profiles for jobPostId: {} with status: {}",
             jobPostId, status, e);
            throw new ServiceException("Không thể đếm hồ sơ ứng tuyển theo trạng thái", e);
        }
    }

    public PageResponse<JobProfile> getAllProfilesByJobPostAndStatus(Long jobPostId, StatusEnum status, Integer pageNo) {
        // Thiết lập Pageable cho phân trang
        Pageable pageable = PageRequest.of(pageNo - 1, 10); // Giả sử mỗi trang có 10 hồ sơ

        // Truy vấn trong repository
        Page<JobProfile> jobProfiles = jobProfileRepository.findByJobPostIdAndStatus(jobPostId, status, pageable);

        // Chuyển đổi từ Page<JobProfile> sang PageResponse<JobProfile> (nếu cần)
        return new PageResponse<>(jobProfiles);
    }

}

