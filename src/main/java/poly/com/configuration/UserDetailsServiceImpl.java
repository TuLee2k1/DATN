package poly.com.configuration;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import poly.com.model.User;
import poly.com.repository.UserRepository;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String userEmail) throws UsernameNotFoundException {

        return userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + userEmail));
    }

    private void validateUserAccount(User user) {
        if (!user.isEnabled()) {
            log.warn("User account is disabled: {}", user.getEmail());
            throw new DisabledException("Tài khoản chưa được kích hoạt");
        }

        if (user.isAccountLocked()) {
            log.warn("User account is locked: {}", user.getEmail());
            throw new LockedException("Tài khoản đã bị khóa");
        }
    }
}
