package poly.com.Enum;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter

public enum Exp {
    NO_EXPERIENCE("Chưa có kinh nghiệm"),
    LESS_THAN_ONE_YEAR("Dưới 1 năm"),
    ONE_YEAR("1 năm"),
    TWO_YEARS("2 năm"),
    THREE_YEARS("3 năm"),
    FOUR_YEARS("4 năm"),
    FIVE_YEARS("5 năm"),
    MORE_THAN_FIVE_YEARS("Hơn 5 năm");

    private final String name;

    Exp(String name) {
        this.name = name;
    }
}
