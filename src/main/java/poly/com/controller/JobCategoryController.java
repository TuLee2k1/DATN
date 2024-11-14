package poly.com.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import poly.com.dto.JobCategoryDto;
import poly.com.model.JobCategory;
import poly.com.service.JobCategoryService;
import poly.com.service.MapValidationErrorService;
@Tag(name = "JobCategory Controller")
@RestController
@RequestMapping("/api/v1/JobCategory")
public class JobCategoryController {

    @Autowired
    JobCategoryService jobCategoryService;

    @Autowired
    MapValidationErrorService mapValidationErrorService;

    //CREATE
    @Operation(summary = "Add new JobCategory", description = "API add new JobCategory")
    @PostMapping
    public ResponseEntity<?> createJobCategory(@Valid @RequestBody JobCategoryDto dto,
                                               BindingResult result) {

        ResponseEntity<?> responseEntity = mapValidationErrorService.mapValidationFields(result);

        if (responseEntity != null){
            return responseEntity;
        }

        JobCategory entity = new JobCategory();
        BeanUtils.copyProperties(dto, entity);

        entity = jobCategoryService.save(entity);

        dto.setId(entity.getId());

        return new ResponseEntity<>(dto, HttpStatus.CREATED);

    }


    //UPDATE
    @Operation(summary = "Update Company", description = "API Update company")
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateJobCategory( @PathVariable ("id") Long id,
                                                @RequestBody JobCategoryDto dto) {
        JobCategory entity = new JobCategory();
        BeanUtils.copyProperties(dto, entity);

        entity = jobCategoryService.update(id, entity);

        dto.setId(entity.getId());

        return new ResponseEntity<>(dto, HttpStatus.CREATED);

    }

    //GET ALL
    @Operation(summary = "Get all Company", description = "API get all company")
    @GetMapping()
    public ResponseEntity<?> getJobCategory() {
        return new ResponseEntity<>(jobCategoryService.findAll(), HttpStatus.OK);
    }


    //GET BY ID
    @Operation(summary = "Get Company with ID", description = "API get company with id")
    @GetMapping("/{id}/get")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        return new ResponseEntity<>(jobCategoryService.findById(id), HttpStatus.OK);
    }


    //DELETE
    @Operation(summary = "Delete Company", description = "API delete company")
    @DeleteMapping("/{id}/get")
    public ResponseEntity<?> deleteJobCategory(@PathVariable("id") Long id) {
        jobCategoryService.delete(id);

        return new ResponseEntity<>("Job Category with " + id + " deleted successfully", HttpStatus.OK);
    }

}
