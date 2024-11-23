package poly.com.Enum;

import org.springframework.security.core.GrantedAuthority;

public enum RoleType {
    ROLE_ADMIN, ROLE_USER, ROLE_COMPANY, GrantedAuthority, ROLE_EMPLOYEE;

    public Object getAuthority(org.springframework.security.core.GrantedAuthority grantedAuthority) {
        return grantedAuthority.getAuthority();
    }
}
