package poly.com.Util;

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

    public User getAuthenticatedUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User is not authenticated");
        }
        String userEmail = authentication.getName();
        return userRepository.findByEmail(userEmail)
         .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}