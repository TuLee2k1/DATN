package poly.com.exception;

import lombok.Data;

@Data
public class JobCategoryExceptionResponse {

    private String message;

    public JobCategoryExceptionResponse(String message) {
        this.message = message;
    }
}
