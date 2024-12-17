package poly.com.configuration;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.hibernate.query.sqm.mutation.internal.Handler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Arrays;
import java.util.List;

@Component
public class SessionInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Lấy đường dẫn hiện tại
        String requestURI = request.getRequestURI();

        // Danh sách các đường dẫn không cần kiểm tra session
        List<String> allowedPaths = Arrays.asList("/auth/login", "/auth/authenticate", "/auth/register","**",
                "/css/", "/js/", "/images/","/Tim-kiem");

        // Kiểm tra xem đường dẫn hiện tại có thuộc danh sách được phép không
        boolean isAllowedPath = allowedPaths.stream()
                .anyMatch(path -> requestURI.startsWith(path));

        if (isAllowedPath) {
            return true;
        }

        // Kiểm tra session
//        HttpSession session = request.getSession(false);
//        if (session == null || session.getAttribute("user") == null) {
//            response.sendRedirect("/auth/login");
//            return false;
//        }

        return true;
    }
}
