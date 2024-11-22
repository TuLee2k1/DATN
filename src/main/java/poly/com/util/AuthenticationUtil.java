package poly.com.util;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import poly.com.exception.UserNotFoundException;
import poly.com.model.User;
import poly.com.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class AuthenticationUtil {

    private final UserRepository userRepository;

    public User getAuthenticatedUser () {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ghi log thông tin xác thực
        System.out.println("Authentication: " + authentication);

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User  is not authenticated");
        }

        String userEmail = authentication.getName();
        System.out.println("Authenticated user email: " + userEmail);

        // Tìm người dùng trong cơ sở dữ liệu
        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User  not found for email: " + userEmail));
    }
}