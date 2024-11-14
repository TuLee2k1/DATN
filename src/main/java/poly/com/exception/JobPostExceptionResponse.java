package poly.com.exception;

import lombok.Data;

@Data
public class JobPostExceptionResponse {
    private String message;
    public JobPostExceptionResponse(String message) {
        this.message = message;
    }
}
