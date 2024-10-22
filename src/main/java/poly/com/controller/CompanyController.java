package poly.com.controller;

import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import poly.com.dto.CompanyDto;
import poly.com.model.Company;
import poly.com.service.CompanyService;
import poly.com.service.FileStorageService;
import poly.com.service.MapValidationErrorService;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/v1/company")
@CrossOrigin
public class CompanyController {
    @Autowired
    CompanyService companyService;

    @Autowired
    MapValidationErrorService mapValidationErrorService;

    @Autowired
    private FileStorageService fileStorageService;



    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE},
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createCompany(@Valid @ModelAttribute CompanyDto dto,
                                           BindingResult result) {

        ResponseEntity<?> responseEntity= mapValidationErrorService.mapValidationFields(result) ;

        if (responseEntity != null){
            return responseEntity;
        }

        Company entity = companyService.save(dto);
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAddress(entity.getAddress());
        dto.setPhone(entity.getPhone());
        dto.setEmail(entity.getEmail());
        dto.setTax_code(entity.getTax_code());
        dto.setLogo(entity.getLogo());


        return new ResponseEntity<>(entity, HttpStatus.CREATED);
//
    }



    @PatchMapping("/{id}")
    public ResponseEntity<?> updateCompany(@PathVariable("id")Long id,
                                           @RequestBody CompanyDto dto) {
        Company entity = new Company();
        BeanUtils.copyProperties(dto, entity);

        entity = companyService.update(id, entity);

        dto.setId(entity.getId());

        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }


    @GetMapping()
    public ResponseEntity<?> getCompany(){
        return new ResponseEntity<>(companyService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/page")
    public ResponseEntity<?> getCompany(
            @PageableDefault(size = 5, sort = "name", direction = Sort.Direction.ASC)
            Pageable pageable){

        return new ResponseEntity<>(companyService.findAll(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}/get")
    public ResponseEntity<?> getCompany(@PathVariable("id")Long id){

        return new ResponseEntity<>(companyService.findById(id), HttpStatus.OK);
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCompany(@PathVariable("id")Long id){
        companyService.deleteById(id);

        return new ResponseEntity<>("Company with Id " + id + " was deleted", HttpStatus.OK);
    }
}
