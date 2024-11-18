package poly.com.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import poly.com.Enum.StatusEnum;
import poly.com.dto.CompanyDto;
import poly.com.dto.JobPostDto;
import poly.com.dto.request.JobPost.JobPostRequest;
import poly.com.dto.request.JobPost.JobPostTitleResponse;
import poly.com.dto.request.PageRequestDTO;
import poly.com.dto.response.JobPost.JobListingResponse;
import poly.com.dto.response.JobPost.JobPostResponse;
import poly.com.dto.response.PageResponse;
import poly.com.model.Company;
import poly.com.model.JobPost;
import poly.com.service.JobPostService;
import poly.com.service.MapValidationErrorService;

import java.util.List;

@Tag(name = "JobPost Controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/Company")
public class JobPostController {

    private final JobPostService jobPostService;

//    @Autowired
//    JobPostService jobPostService;
//
//    @Autowired
//    MapValidationErrorService mapValidationErrorService;
//
//    //CREATE
//    @Operation(summary = "Add new JobPost", description = "API add new JobPost")
//    @PostMapping
//    public ResponseEntity<?> createJobPost(@Valid @RequestBody JobPostDto dto,
//                                           BindingResult result) {
//
//        ResponseEntity<?> responseEntity= mapValidationErrorService.mapValidationFields(result) ;
//
//        if (responseEntity != null){
//            return responseEntity;
//        }
//
//        JobPost entity = new JobPost();
//        BeanUtils.copyProperties(dto, entity);
//
//        entity = jobPostService.save(entity);
//        dto.setId(entity.getId());
//        return new ResponseEntity<>(dto, HttpStatus.CREATED);
//    }
//    //GET ALL
//    @Operation(summary = "Get All JobPost", description = "API get all JobPost")
//    @GetMapping()
//    public ResponseEntity<?> getJobPostAll(){
//        return new ResponseEntity<>(jobPostService.findAll(), HttpStatus.OK);
//    }
//
//    // PAGE
//    @Operation(summary = "Get All JobPost Pageable", description = "API get all JobPost Pageable")
//    @GetMapping("/page")
//    public ResponseEntity<?> getJobPostPage(
//            @PageableDefault(size = 3, sort = "jobTitle", direction = Sort.Direction.ASC)
//            Pageable pageable){
//
//        return new ResponseEntity<>(jobPostService.findAll(pageable), HttpStatus.OK);
//    }
//
//    //UPADATE
//    @Operation(summary = "Update JobPost", description = "API Update JobPost")
//    @PatchMapping("/{id}")
//    public ResponseEntity<?> updateJobPost(@PathVariable("id")Long id,
//                                           @RequestBody JobPost dto) {
//        JobPost entity = new JobPost();
//        BeanUtils.copyProperties(dto, entity);
//
//        entity = jobPostService.update(id, entity);
//
//        dto.setId(entity.getId());
//
//        return new ResponseEntity<>(dto, HttpStatus.CREATED);
//    }
//
//
//    //GET ID
//    @Operation(summary = "Get JobPost With ID", description = "API get JobPost with id")
//    @GetMapping("/{id}/get")
//    public ResponseEntity<?> getJobPostId(@PathVariable("id")Long id){
//
//        return new ResponseEntity<>(jobPostService.findById(id), HttpStatus.OK);
//    }
//
//    //DELETE
//    @Operation(summary = "Delete JobPost", description = "API delete JobPost")
//    @DeleteMapping("/{id}")
//    public ResponseEntity<?> deleteJobPost(@PathVariable("id")Long id){
//        jobPostService.deleteById(id);
//
//        return new ResponseEntity<>("Jop Post with Id " + id + " was deleted", HttpStatus.OK);
//    }

    @PostMapping("/createJobPost") // /Company/createJobPost
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<?> createJobPost(@Valid @RequestBody JobPostRequest request) {
        try {
            JobPostResponse response = jobPostService.createJobPost(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @GetMapping("/getJobPost") // /Company/getJobPost?id=1
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<?> getJobPost(@RequestParam Long id) {
        try {
            JobPostResponse response = jobPostService.getJobPost(id);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('COMPANY')") // /Company/updateJobPost/1
    @PutMapping("/{id}")
    public ResponseEntity<JobPostResponse> updateJobPost(@PathVariable Long id, @RequestBody JobPostRequest request) {
        JobPostResponse response = jobPostService.updateJobPost(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}") // /Company/deleteJobPost/1
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<?> deleteJobPost(@PathVariable Long id) {
        try {
            jobPostService.deleteById(id);
            return ResponseEntity.ok("Job Post with Id " + id + " was deleted");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/disable")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<String> disableJobPost(@PathVariable Long id) {
        jobPostService.disableView(id);
        return ResponseEntity.ok("Job Post with Id " + id + " was disabled");
    }

    @PatchMapping("/{id}/enable")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<String> enableJobPost(@PathVariable Long id) {
        jobPostService.enabledView(id);
        return ResponseEntity.ok("Job Post with Id " + id + " was enabled");
    }

    @GetMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<List<JobListingResponse>> getJobPostList() {
        List<JobListingResponse> responses = jobPostService.getJobListings();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/page")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<Page<JobListingResponse>> getJobPostPage(
     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        Page<JobListingResponse> responses = jobPostService.getJobListings(pageNo);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<PageResponse<JobListingResponse>> searchJobPost(
     @RequestParam(name = "jobTitle", defaultValue = "") String jobTitle,
     @RequestParam(name = "statusEnum", defaultValue = "PENDING") String statusEnumString,
     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {

        StatusEnum statusEnum = StatusEnum.valueOf(statusEnumString.toUpperCase());

        PageResponse<JobListingResponse> responses = jobPostService.getJobListings(jobTitle, statusEnum, pageNo);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/getJobPostTitle")
   public ResponseEntity<List<JobPostTitleResponse>> getJobPostTitle() {
        List<JobPostTitleResponse> responses = jobPostService.getJobPostTitle();
        return ResponseEntity.ok(responses);
    }



}
