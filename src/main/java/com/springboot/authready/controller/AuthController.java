package com.springboot.authready.controller;

import com.springboot.authready.entity.User;
import com.springboot.authready.model.*;
import com.springboot.authready.repository.UserRepository;
import com.springboot.authready.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController @AllArgsConstructor @RequestMapping("/api/auth/")
public class AuthController {

    private final UserRepository userRepository;
    private final UserService userService;
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginModel loginRequest) {
        String jwt = userService.authenticate(loginRequest);
        HttpHeaders headers = new HttpHeaders();
        headers.add("token", "Bearer " +jwt);
        return ResponseEntity.ok().headers(headers).body(null);
    }

    @GetMapping("/logout")
    public ResponseEntity<?> logout(){
        userService.logout();
        return ResponseEntity.ok("Logout successful.");
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupModel signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity
                    .badRequest()
                    .body("Error: Email is already in use!");
        }
        userService.register(signUpRequest);
        return ResponseEntity.ok("Registration successful.");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody EmailModel model){
            userService.updateResetPasswordToken(model.getEmail());
        return ResponseEntity.ok("An e-mail has been sent to recover your password.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String token, @RequestBody ResetPasswordModel model){
        User user = userService.getByResetPasswordToken(token);
        //  will refactor later to let frontend handle this
        if (Objects.nonNull(user) && model.getNewPassword().equals(model.getRepeatPassword())){
            userService.updatePassword(user, model.getNewPassword());
        }
        return ResponseEntity.ok("All done.");
    }

    @PutMapping("/edit-profile")
    public ResponseEntity<?> updateProfile(@RequestBody UpdateProfileModel user){
        userService.update(user);
        return ResponseEntity.ok("Profile updated successfully.");
    }
}
