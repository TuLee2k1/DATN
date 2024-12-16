package poly.com.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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

    public boolean unlockAccount(Long userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setAccountLocked(false); // Đặt lại trạng thái mở khóa
            userRepository.save(user);  // Lưu lại thay đổi
            return true;
        }
        return false; // Không tìm thấy người dùng
    }

    public Page<User> users(String email,Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 5);


        return userRepository.findByEmailContaining(email,pageable);
    }
}
