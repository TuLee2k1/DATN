package poly.com.controller;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import poly.com.dto.AuthRegisterDTO;
import poly.com.dto.AuthenticationDTO;
import poly.com.dto.UserDTO;
import poly.com.dto.response.AuthenResponse;
import poly.com.exception.ExceptionResponse;
import poly.com.service.AuthenticationService;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<Void> createUser(@Valid @RequestBody AuthRegisterDTO authRegisterDTO) throws IllegalAccessException,
    MessagingException {
        authenticationService.register(authRegisterDTO);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenResponse> authenticate(@RequestBody @Valid AuthenticationDTO authenticationDTO) {
        return ResponseEntity.ok(authenticationService.authenticate(authenticationDTO));
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
}
