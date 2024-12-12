package poly.com.Enum;

import lombok.Getter;

@Getter
public enum ApplicationStatusEnum {
    INTERVIEW_SCHEDULED("Đã Lên Lịch Phỏng Vấn"),
    INTERVIEW_PASSED("Qua Vòng Phỏng Vấn"),
    INTERVIEW_FAILED("Không Qua Phỏng Vấn"),
    REJECTED("Bị Từ Chối"),
    HIRED("Đã Tuyển Dụng"),
    ARCHIVED("Đã Lưu Trữ"),
    DELETED("Đã Xóa");

    private final String displayName;

    ApplicationStatusEnum(String displayName) {
        this.displayName = displayName;
    }
}
