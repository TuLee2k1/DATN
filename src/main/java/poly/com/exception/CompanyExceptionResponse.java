package poly.com.exception;

import lombok.Data;

@Data
public class CompanyExceptionResponse {
   private String message;

    public CompanyExceptionResponse(String message) {
        this.message = message;
    }
}
