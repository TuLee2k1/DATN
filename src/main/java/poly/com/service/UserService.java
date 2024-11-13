//package poly.com.service;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//import poly.com.Enum.ProviderEnum;
//import poly.com.dto.UserDTO;
//import poly.com.dto.response.UserResponse;
//import poly.com.model.Role;
//import poly.com.model.User;
//import poly.com.repository.RoleRepository;
//import poly.com.repository.UserRepository;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class UserService {
//
//    private final UserRepository userRepository;
//    private final PasswordEncoder passwordEncoder;
//    private final RoleRepository roleRepository;
//
//    public UserResponse registerUserLocal(UserDTO userDTO) {
//        // Kiểm tra xem email đã tồn tại trong cơ sở dữ liệu chưa
//        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
//            throw new RuntimeException("Email already exists");
//        }
//
//        // Tạo một đối tượng User mới
//        User user = new User();
//        user.setEmail(userDTO.getEmail());
//        user.setName(userDTO.getName());
//        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
//        user.setProvider(ProviderEnum.LOCAL);
//
//        // Lưu đối tượng User vào cơ sở dữ liệu
//        user = userRepository.save(user);
//
//        // Tạo một đối tượng Role mới và liên kết với User
//        Role role = new Role();
//        role.setRole(userDTO.getRole()); // Thiết lập vai trò mặc định là USER
//  //      role.setUsers(Collections.singletonList(user)); // Liên kết Role với User vừa tạo
//        roleRepository.save(role); // Lưu đối tượng Role vào cơ sở dữ liệu
//
//        // Trả về thông tin User dưới dạng UserResponse
//        return convertToResponse(user);
//    }
//
//    public UserResponse loginLocal(UserDTO userDTO) {
//        User user = userRepository.findByEmail(userDTO.getEmail())
//        .orElseThrow(() -> new RuntimeException("User not found"));
//        if (!passwordEncoder.matches(userDTO.getPassword(), user.getPassword())) {
//            throw new RuntimeException("Password is incorrect");
//        }
//        return convertToResponse(user);
//    }
//
//    public UserResponse loginRegisterGoogle(OAuth2AuthenticationToken oAuth2AuthenticationToken) {
//        OAuth2User oAuth2User = oAuth2AuthenticationToken.getPrincipal();
//
//        // In toàn bộ thuộc tính để kiểm tra
//        oAuth2User.getAttributes().forEach((key, value) -> {
//            log.info("Attribute: " + key + " = " + value);
//        });
//
//        // Thử lấy các trường có thể chứa tên người dùng
//        String email = oAuth2User.getAttribute("email");
//        String name = oAuth2User.getAttribute("name");  // Thử lấy tên từ trường "name"
//        if (name == null) {
//            // Nếu không có trường "name", thử với các trường khác
//            name = oAuth2User.getAttribute("given_name") + " " + oAuth2User.getAttribute("family_name");
//        }
//
//        log.info("User Email: " + email);
//        log.info("User Name: " + name);
//
//        System.out.println("User Email: " + email);
//        System.out.println("User Name: " + name);
//        User user = userRepository.findByEmail(email).orElse(null);
//        if (user == null) {
//            user = new User();
//            user.setEmail(email);
//            user.setName(name);
//            user.setProvider(ProviderEnum.GOOGLE);
//            // Giả sử bạn có thuộc tính `username` trong User
//            user = userRepository.save(user);
//        }
//        return convertToResponse(user);
//    }
//
//    public UserResponse convertToResponse(User user) {
//        return UserResponse.builder()
//        .email(user.getEmail())
//        .name(user.getName())
//        .build();
//    }
//}