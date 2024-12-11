package poly.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import poly.com.model.User;
import poly.com.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public boolean lockAccount(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAccountLocked(true); // Cập nhật trạng thái khóa
            userRepository.save(user);  // Lưu lại thay đổi
            return true;
        }
        return false; // Không tìm thấy người dùng
    }
}
