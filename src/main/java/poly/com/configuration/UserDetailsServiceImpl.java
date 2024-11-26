package poly.com.configuration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import poly.com.model.Role;
import poly.com.model.User;
import poly.com.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Tìm kiếm người dùng với email: " + username);
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với email: " + username));
        System.out.println("Người dùng tìm thấy: " + user.getEmail());
        System.out.println("Mật khẩu đã mã hóa: " + user.getPassword());
        // Trả về đối tượng UserDetails từ lớp User của bạn
        return user;
//        new org.springframework.security.core.userdetails.User(
//                user.getEmail(),
//                user.getPassword(),
//                user.isEnabled(), // Trạng thái tài khoản
//                user.isAccountNonExpired(), // Tài khoản không hết hạn
//                user.isCredentialsNonExpired(), // Thông tin xác thực không hết hạn
//                user.isAccountNonLocked(), // Tài khoản không bị khóa
//                mapRolesToAuthorities(user.getRoles()) // Chuyển đổi vai trò thành quyền
//        );
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(List<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
                .collect(Collectors.toList());
    }
}
