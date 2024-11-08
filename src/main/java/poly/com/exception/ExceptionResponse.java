package poly.com.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;

@Component
@Builder
@Data
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"status", "message", "Result"})
public class ExceptionResponse<T> {

    int status;
    String message;
    T Result;

    public ExceptionResponse() {
    }

    public ExceptionResponse(int status, String message, T result) {
        this.status = status;
        this.message = message;
        Result = result;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return Result;
    }

    public void setResult(T result) {
        Result = result;
    }
}
