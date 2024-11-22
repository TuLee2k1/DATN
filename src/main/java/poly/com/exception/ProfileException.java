package poly.com.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ProfileException extends RuntimeException {
    public ProfileException(String message) {
        super(message);
    }
}
