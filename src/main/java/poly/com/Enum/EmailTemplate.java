package poly.com.Enum;

import lombok.Getter;

@Getter
public enum EmailTemplate {
    ACTIVATION_ACCOUNT("activation_account"),
    FORGOT_PASSWORD("forgot_password");

    private final String name;

    EmailTemplate(String name) {
        this.name = name;
    }
}
