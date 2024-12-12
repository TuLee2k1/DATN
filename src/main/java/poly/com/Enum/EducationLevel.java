package poly.com.Enum;

import lombok.Getter;

@Getter
public enum EducationLevel {
    POSTGRADUATE("Trên đại học"),
    UNIVERSITY("Đại học"),
    COLLEGE("Cao đẳng"),
    INTERMEDIATE("Trung cấp"),
    HIGH_SCHOOL("Trung học"),
    VOCATIONAL_CERTIFICATE("Chứng chỉ nghề");

    private final String name;

    EducationLevel(String description) {
        this.name = description;
    }


}
