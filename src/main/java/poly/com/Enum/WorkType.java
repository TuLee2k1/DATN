package poly.com.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;

@Getter
    public enum WorkType
    {
        FULL_TIME("Toàn thời gian"), // toàn thời gian
        PART_TIME("Bán thời gian"), // bán thời gian
        INTERNSHIP("Thực tập"), // thực tập
        FREELANCE("Làm việc tự do"), // làm việc tự do
        REMOTE("Làm việc từ xa"), // làm việc từ xa
        CONTRACT("Hợp đồng"), // hợp đồng
        TEMPORARY("Tạm thời"), // tạm thời
        VOLUNTEER("Tình nguyện"), // tình nguyện
        APPRENTICESHIP("Học việc"); // học việc

        private final String name;

        WorkType(String name) {
        this.name = name;
    }

    }