package poly.com.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter

public enum JobLevel {
    JUNIOR("Nhân viên mới"),
    MID("Nhân viên trung cấp"),
    SENIOR("Nhân viên cao cấp"),
    LEAD("Trưởng nhóm"),
    MANAGER("Quản lý"),
    DIRECTOR("Giám đốc"),
    EXECUTIVE("Giám đốc điều hành");

    private final String name;

    JobLevel(String name) {
        this.name = name;
    }
}