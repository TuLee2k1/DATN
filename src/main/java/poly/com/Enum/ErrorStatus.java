package poly.com.Enum;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
public enum ErrorStatus {
    ACCOUNT_NOT_FOUND(404, "Account not found"),
    ACCOUNT_LOCKED(423, "Account locked"),
    INCORRECT_PASSWORD(300, "Incorrect password"),
    INVALID_TOKEN(401, "Invalid token"),
    NEW_PASSWORD_DOES_NOT_MATH(301, "New password does not match"),
    BAD_CREDETIALS(304, "login or password is incorrect"),
    ;
     final int status;
     final String message;

    ErrorStatus(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
