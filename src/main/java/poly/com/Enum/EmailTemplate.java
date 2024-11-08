package poly.com.Enum;

import lombok.Getter;

@Getter
public enum EmailTemplate {
    ACTICATION_ACCOUNT("activation_account");

    private final String name;

    EmailTemplate(String name) {
        this.name = name;
    }
}
