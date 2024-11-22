package poly.com.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
public class ErrorDto {

    private final int status;
    private final String error;
    private final String message;
    private List<String> detailedMessages;

    public ErrorDto(HttpStatus httpStatus, String message) {
        status = httpStatus.value();
        error = httpStatus.getReasonPhrase();
        this.message = message;
    }

}
