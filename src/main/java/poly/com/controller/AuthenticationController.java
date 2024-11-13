package poly.com.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import poly.com.dto.request.Auth.AuthRegisterRequest;
import poly.com.dto.request.Auth.LoginRequest;
import poly.com.dto.request.Auth.RegisterCompanyRequest;
import poly.com.dto.request.Auth.UserChangepasswordDTO;
import poly.com.dto.response.Auth.AuthenticationResponse;
import poly.com.exception.ApiResponse;
import poly.com.service.AuthenticationService;

import java.io.IOException;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register/user")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<AuthenticationResponse> createUser(@Valid @RequestBody AuthRegisterRequest authRegisterRequest)
     throws IllegalAccessException, MessagingException {
        var result = authenticationService.registerUser(authRegisterRequest);
        return ApiResponse.<AuthenticationResponse>builder()
                .status(HttpStatus.OK.value())
                .message("User registered successfully")
                .Result(result)
                .build();
    }

    @PostMapping("/register/company")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ApiResponse<AuthenticationResponse> createCompany(@Valid @RequestBody RegisterCompanyRequest request) throws IllegalAccessException,
    MessagingException {
        //authenticationService.registerCompany(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Company registered successfully")
         .Result(authenticationService.registerCompany(request))
                .build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody @Valid LoginRequest loginRequest) {
        return ResponseEntity.ok(authenticationService.authenticate(loginRequest));
    }

    @PostMapping("/refresh-token")
    public void  refreshToken(HttpServletRequest request,
                              HttpServletResponse response) throws IOException {
        authenticationService.refreshToken(request, response);
    }

    @GetMapping("/activate-account")
    public ResponseEntity<Void> activateAccount(@RequestParam String token) throws MessagingException {
        authenticationService.activateAccount(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/resend-verification-code")
    public ResponseEntity<String> resendVerificationCode(@RequestParam @Valid String email) {
        try {
            authenticationService.resendVerificationCode(email);
            return ResponseEntity.ok("Verification code sent successfully");
        } catch (MessagingException e) {
            return status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send verification code");
        }
    }

    @PostMapping("/change-password")
    public ApiResponse<AuthenticationResponse> changePassword(@RequestBody @Valid UserChangepasswordDTO request) {
            authenticationService.changePassword(request);
            return ApiResponse.<AuthenticationResponse>builder()
                    .status(HttpStatus.OK.value())
                    .message("Password changed successfully")
                    .build();

    }

    @PostMapping("/forgot-password")
    public ApiResponse<AuthenticationResponse> forgotPassword(@RequestParam @Valid String email) throws MessagingException {
        authenticationService.forgotPassword(email);
        return ApiResponse.<AuthenticationResponse>builder()
                .status(HttpStatus.OK.value())
                .message("Password reset link sent successfully")
                .build();
    }

    @PreAuthorize("hasRole('ROLE_COMPANY')")
    @GetMapping("/company")
    public ApiResponse<String> com(){
        return ApiResponse.<String>builder()
                .status(HttpStatus.OK.value())
                .message("Company")
                .build();
    }
}
