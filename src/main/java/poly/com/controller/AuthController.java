package poly.com.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import poly.com.Enum.RoleType;
import poly.com.dto.UserDTO;
import poly.com.dto.response.UserResponse;
import poly.com.exception.ExceptionResponse;
import poly.com.service.UserService;

import java.io.IOException;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("register/user")
    public ExceptionResponse<UserResponse> registerUser(@RequestBody @Validated UserDTO userDTO) {
        userDTO.setRole(RoleType.ROLE_USER);
        return ExceptionResponse.<UserResponse>builder().status(HttpStatus.OK.value()).Result(userService.registerUserLocal(userDTO)).build();
    }

    @PostMapping("register/company")
    public ExceptionResponse<UserResponse> registerCompany(@RequestBody @Validated UserDTO userDTO) {
        userDTO.setRole(RoleType.ROLE_COMPANY);
        return ExceptionResponse.<UserResponse>builder().status(HttpStatus.OK.value()).Result(userService.registerUserLocal(userDTO)).build();
    }

    @PostMapping("register/admin")
    public ExceptionResponse<UserResponse> registerAdmin(@RequestBody @Validated UserDTO userDTO) {
        userDTO.setRole(RoleType.ROLE_ADMIN);
        return ExceptionResponse.<UserResponse>builder().status(HttpStatus.OK.value()).Result(userService.registerUserLocal(userDTO)).build();
    }

    @PostMapping("register/employee")
    public ExceptionResponse<UserResponse> registerEmployee(@RequestBody @Validated UserDTO userDTO) {
        userDTO.setRole(RoleType.ROLE_EMPLOYEE);
        return ExceptionResponse.<UserResponse>builder().status(HttpStatus.OK.value()).Result(userService.registerUserLocal(userDTO)).build();
    }


    @PostMapping("/login/local")
    public ExceptionResponse<UserResponse> loginLocal(@RequestBody @Validated UserDTO userDTO) {
        return ExceptionResponse.<UserResponse>builder().Result(userService.loginLocal(userDTO)).build();
    }

    @GetMapping("/login/google")
    public ExceptionResponse<UserResponse> loginGoogle(HttpServletResponse response) throws IOException {
        response.sendRedirect("/oauth2/authorization/google");
        return ExceptionResponse.<UserResponse>builder().message("Redirect to Google login").build();
    }

    @GetMapping("/loginSuccess")
    public ResponseEntity<? > handleGoogleSuccess(OAuth2AuthenticationToken oAuth2AuthenticationToken){
        UserResponse user = userService.loginRegisterGoogle(oAuth2AuthenticationToken);
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create("http://localhost:8080/home")).build();
    }
}
