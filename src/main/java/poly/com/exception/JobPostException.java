package poly.com.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class JobPostException extends RuntimeException{
    public JobPostException(String message) {
        super(message);
    }
}
