package poly.com.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ExperienceException extends RuntimeException {
    public ExperienceException(String message) {
        super(message);
    }
}
