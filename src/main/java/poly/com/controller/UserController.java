package poly.com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import poly.com.dto.UserDTO;


import java.util.List;


public class UserController {
//    @Autowired
//    private UserService userService;
//
//    @PostMapping
//    public ResponseEntity<User> createUser(@RequestBody UserDTO userDTO) {
//        User user = userService.save(userDTO);
//        return new ResponseEntity<>(user, HttpStatus.CREATED);
//    }
//
//    @GetMapping("/all") // Lấy tất cả người dùng
//    public ResponseEntity<List<User>> getAllUsers() {
//        List<User> users = userService.findAll();
//        return new ResponseEntity<>(users, HttpStatus.OK);
//    }
//
//    @GetMapping // Phân trang
//    public ResponseEntity<Page<User>> getAllUsersPaginated(@RequestParam(defaultValue = "0") int page,
//                                                           @RequestParam(defaultValue = "10") int size) {
//        Page<User> users = userService.findAll(PageRequest.of(page, size));
//        return new ResponseEntity<>(users, HttpStatus.OK);
//    }
//
//    @GetMapping("/search")
//    public ResponseEntity<User> getUserByIdAndUsername(@RequestParam Long id, @RequestParam String username) {
//        User user = userService.findByIdAndUsername(id, username);
//        return user != null ?
//                new ResponseEntity<>(user, HttpStatus.OK) :
//                new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//
//    @PatchMapping("/{id}")
//    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
//        User updatedUser = userService.update(id, userDTO);
//        return updatedUser != null ?
//                new ResponseEntity<>(updatedUser, HttpStatus.OK) :
//                new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
//        boolean isDeleted = userService.delete(id);
//        return isDeleted ? new ResponseEntity<>(HttpStatus.NO_CONTENT) :
//                new ResponseEntity<>(HttpStatus.NOT_FOUND);
//    }
}
