package poly.com.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class JobCategoryException extends RuntimeException {
    public JobCategoryException(String message) {
        super(message);
    }
}
