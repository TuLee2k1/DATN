
package poly.com.Util;


import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import poly.com.exception.UserNotFoundException;
import poly.com.model.User;
import poly.com.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class AuthenticationUtil {
    private final UserRepository userRepository;
    private final HttpSession session;

    public User getAuthenticatedUser() {
        // Lấy email từ authentication hiện tại
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new RuntimeException("Người dùng chưa đăng nhập");
        }

        // Lấy username (email) từ authentication
        String userEmail = authentication.getName();

        // Tìm user trong database bằng email
        return userRepository.findByEmail(userEmail)
         .orElseThrow(() -> new UserNotFoundException("Không tìm thấy thông tin người dùng"));
    }

    public Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public User getCurrentUser() {
        String userEmail = getAuthentication().getName();
        return userRepository.findByEmail(userEmail)
         .orElseThrow(() -> new UserNotFoundException("Không tìm thấy thông tin người dùng"));
    }

}