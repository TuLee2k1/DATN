package poly.com.exception;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler  {

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ApiResponse<String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
//        String errors = ex.getBindingResult().getFieldErrors().stream()
//         .map(FieldError::getDefaultMessage)
//         .collect(Collectors.joining(", "));
//
//        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
//         .status(HttpStatus.BAD_REQUEST.value())
//         .message("Validation failed")
//         .Result(errors)
//         .build();
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiResponse);
//    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<String>> handleRuntimeException(RuntimeException ex) {
        ApiResponse<String> apiResponse = ApiResponse.<String>builder()
         .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
         .message(ex.getMessage())
         .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiResponse);
    }

    @ExceptionHandler(ValidationException.class)
    public String handleValidationException(ValidationException ex, Model model) {
        log.error("Validation error: ", ex);
        model.addAttribute("errorMessages", ex.getMessage());
        return "error/validation-error"; // Trang hiển thị lỗi
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleMethodArgumentNotValidException(
     MethodArgumentNotValidException ex,
     Model model
    ) {
        List<String> errors = ex.getBindingResult()
         .getFieldErrors()
         .stream()
         .map(FieldError::getDefaultMessage)
         .collect(Collectors.toList());

        model.addAttribute("errorMessages", errors);
        return "error/validation-error";
    }
}