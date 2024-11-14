package poly.com.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import poly.com.model.JobProfile;
import poly.com.service.JobProfileService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
@Tag(name = "JobProfile Controller")
@RestController
@RequestMapping("/api/jobProfiles")
public class JobProfileController {

    private static String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/fileCV";

    @Autowired
    private JobProfileService jobProfileService;

    // Lấy tất cả các jobProfiles
    @Operation(summary = "Get All JobProfile", description = "API Get JobProfile all")
    @GetMapping
    public ResponseEntity<Page<JobProfile>> getAllJobProfiles(
            @PageableDefault(size = 10) Pageable pageable) {
        Page<JobProfile> jobProfiles = jobProfileService.getAllJobProfiles(pageable);
        return ResponseEntity.ok(jobProfiles);
    }

    @PostConstruct
    public void init() {
        File uploadDir = new File(UPLOAD_DIR);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
            System.out.println("Created upload directory at: " + UPLOAD_DIR);
        }
    }

    // Lấy thông tin jobProfile theo id
    @Operation(summary = "Get JobProfile with ID", description = "API Get JobProfile with ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getJobProfileById(@PathVariable Long id) {
        Optional<JobProfile> jobProfile = jobProfileService.getJobProfileById(id);
        if (jobProfile.isPresent()) {
            return ResponseEntity.ok(jobProfile.get());
        } else {
            return ResponseEntity.status(404).body("JobProfile with ID " + id + " not found.");
        }
    }

    // Tạo mới jobProfile với file PDF
    @Operation(summary = "Add new JobProfile", description = "API create new JobProfile")
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createJobProfile(@RequestParam("fileCV") MultipartFile file,
                                              @RequestParam("job_id") Long jobId,
                                              @RequestParam("profile_id") Long profileId) throws IOException {

        // Lưu file PDF vào thư mục
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(UPLOAD_DIR, fileName);
        Files.write(filePath, file.getBytes());

        // Tạo JobProfile mới và lưu đường dẫn file PDF
        JobProfile jobProfile = new JobProfile();
        jobProfile.setFileCV(fileName);  // Lưu tên file, hoặc bạn có thể lưu đường dẫn đầy đủ

        // Tự động lấy ngày hiện tại
        jobProfile.setDateApply(java.sql.Date.valueOf(LocalDate.now()));

        // Set các thông tin khác như job và profile (tạo các phương thức này trong service nếu chưa có)
//        jobProfile.setJob(jobProfileService.getJobById(jobId));  // Giả sử bạn đã có hàm getJobById trong service
//        jobProfile.setProfile(jobProfileService.getProfileById(profileId));  // Giả sử bạn đã có hàm getProfileById trong service

        JobProfile savedProfile = jobProfileService.saveJobProfile(jobProfile);
        return ResponseEntity.status(201).body("JobProfile created successfully with ID " + savedProfile.getId());
    }

    // Cập nhật jobProfile
    @Operation(summary = "Update JobProfile", description = "API Update JobProfile")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateJobProfile(@PathVariable Long id,
                                              @RequestParam(value = "fileCV", required = false) MultipartFile file,
                                              @RequestParam(value = "dateApply", required = false) String dateApply) throws IOException {
        Optional<JobProfile> optionalJobProfile = jobProfileService.getJobProfileById(id);

        if (optionalJobProfile.isPresent()) {
            JobProfile jobProfile = optionalJobProfile.get();

            // Nếu có file mới được upload, xóa file cũ và lưu file mới
            if (file != null) {
                String oldFileName = jobProfile.getFileCV();
                if (oldFileName != null) {
                    // Xóa file cũ
                    File oldFile = new File(UPLOAD_DIR, oldFileName);
                    if (oldFile.exists()) {
                        oldFile.delete();
                        System.out.println("Deleted old file: " + oldFileName);
                    }
                }

                // Lưu file mới
                String newFileName = file.getOriginalFilename();
                Path newFilePath = Paths.get(UPLOAD_DIR, newFileName);
                Files.write(newFilePath, file.getBytes());

                // Cập nhật đường dẫn file mới trong jobProfile
                jobProfile.setFileCV(newFileName);
            }

            // Cập nhật ngày apply nếu có
            if (dateApply != null) {
                jobProfile.setDateApply(java.sql.Date.valueOf(dateApply));
            }

            JobProfile updatedJobProfile = jobProfileService.saveJobProfile(jobProfile);
            return ResponseEntity.ok("JobProfile with ID " + id + " updated successfully.");
        } else {
            return ResponseEntity.status(404).body("JobProfile with ID " + id + " not found.");
        }
    }

    // Xóa jobProfile
    @Operation(summary = "Delete JobProfile", description = "API Delete JobProfile")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJobProfile(@PathVariable Long id) {
        Optional<JobProfile> optionalJobProfile = jobProfileService.getJobProfileById(id);
        if (optionalJobProfile.isPresent()) {
            jobProfileService.deleteJobProfile(id);
            return ResponseEntity.ok("JobProfile with ID " + id + " deleted successfully.");
        } else {
            return ResponseEntity.status(404).body("JobProfile with ID " + id + " not found.");
        }
    }
}
