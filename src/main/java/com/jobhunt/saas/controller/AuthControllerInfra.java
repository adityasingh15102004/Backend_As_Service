package com.jobhunt.saas.controller;

import com.jobhunt.saas.dto.AppResponse;
import com.jobhunt.saas.dto.LoginRequest;
import com.jobhunt.saas.dto.LoginResponse;
import com.jobhunt.saas.dto.RegistrationRequest;
import com.jobhunt.saas.dto.RegistrationResponse;
import com.jobhunt.saas.service.AuthService;
import com.jobhunt.saas.service.UserService;
import jakarta.persistence.GeneratedValue;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthControllerInfra {

    @Autowired
    private UserService userService;
    @Autowired
    AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AppResponse<RegistrationResponse>> regUser(@Valid @RequestBody RegistrationRequest registrationRequest){
        try {
            RegistrationResponse response = userService.addUser(registrationRequest);
            AppResponse<RegistrationResponse> appResponse =
                    new AppResponse<>("Success", response, 200, LocalDateTime.now());
            return ResponseEntity.ok(appResponse);
        } catch (RuntimeException e) {
            AppResponse<RegistrationResponse> errorResponse =
                    new AppResponse<>(e.getMessage(), null, 400, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AppResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest){
        try {
            LoginResponse response = authService.login(loginRequest);
            AppResponse<LoginResponse> body =
                    new AppResponse<>("Success", response, 200, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.OK).body(body);
        } catch (RuntimeException e) {
            AppResponse<LoginResponse> errorResponse =
                    new AppResponse<>(e.getMessage(), null, 401, LocalDateTime.now());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @GetMapping("/verify-email")
    public ResponseEntity<AppResponse<String>> verifyEmail(@RequestParam String token){

        authService.verifyEmail(token);
        AppResponse<String> body=
                new AppResponse<>("Email Verified Successfully",
                        null,
                        200,
                        LocalDateTime.now()
                );
         return ResponseEntity.ok(body);
    }

    // Temporary admin endpoint to fix existing unverified users
    @GetMapping("/admin/verify-all-users")
    public ResponseEntity<AppResponse<String>> verifyAllUsers(){
        int count = userService.verifyAllExistingUsers();
        String msg = "Verified " + count + " users successfully.";
        return ResponseEntity.ok(new AppResponse<>(msg, null, 200, LocalDateTime.now()));
    }
}
