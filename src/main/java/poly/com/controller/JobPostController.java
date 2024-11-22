//package poly.com.controller;
//
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.web.PageableDefault;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import poly.com.model.JobPost;
//import poly.com.service.MapValidationErrorService;
//@Tag(name = "JobPost Controller")
//@RestController
//@RequestMapping("/api/v1/jobpost")
//public class JobPostController {
//
////    @Autowired
////    JobPostService jobPostService;
//
//    @Autowired
//    MapValidationErrorService mapValidationErrorService;
//
//    //CREATE
////    @PostMapping("/createJobPost") // /Company/createJobPost
//////    @PreAuthorize("hasRole('COMPANY')")
////    public ResponseEntity<?> createJobPost(@Valid @RequestBody JobPostRequest request) {
////        try {
////            JobPostResponse response = jobPostService.createJobPost(request);
////            return ResponseEntity.ok(response);
////        } catch (Exception e) {
////            return ResponseEntity.badRequest()
////                    .body(e.getMessage());
////        }
////    }
//
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
//
//}
