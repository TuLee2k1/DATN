package poly.com.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@RestController
public class CustomResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CompanyException.class)
    public final ResponseEntity<Object> handleCompanyException(CompanyException ex, WebRequest request) {
            CompanyExceptionResponse exceptionResponse = new CompanyExceptionResponse(ex.getMessage());

            return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JobCategoryException.class)
    public final ResponseEntity<Object> handleJobCategoryException(JobCategoryException ex, WebRequest request) {
        JobCategoryExceptionResponse exceptionResponse = new JobCategoryExceptionResponse(ex.getMessage());

        return new ResponseEntity<>(exceptionResponse, HttpStatus.BAD_REQUEST);
    }
}
