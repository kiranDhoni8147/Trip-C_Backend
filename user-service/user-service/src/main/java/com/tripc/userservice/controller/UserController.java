package com.tripc.userservice.controller;

import com.tripc.userservice.dto.*;
import com.tripc.userservice.exception.InvalidOrExpiredOtpException;
import com.tripc.userservice.exception.LoginException;
import com.tripc.userservice.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/user")
public class UserController{

    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody @Valid UserDto userDto){
        try{
            userService.register(userDto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Registration Successfull,Please Login To Continue");
        }
        catch (RuntimeException ex) {
            logger.error("Registration Error", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registration failed. Please check your input and try again.");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto loginRequestDto){
        try {
            String token = userService.login(loginRequestDto);
            return ResponseEntity.ok(new LoginResponseDto("success", "Login successful", token));
        } catch ( LoginException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponseDto("error", "Login failed. Invalid credentials."));
        }
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<String>> getBookings(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String username = authentication.getName();
        List<String> bookings = new ArrayList<>();
        bookings.add(username + "'s Bookings retrieved");

        return ResponseEntity.ok(bookings);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestBody @Valid ForgetPasswordRequestDto forgetPasswordRequestDto){
        try{
            userService.forgotPassword(forgetPasswordRequestDto.getPhoneNumber());
            return ResponseEntity.ok("otp Sent Successfully");

        }catch (RuntimeException ex) {
            logger.error("Forgot Password Error", ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to initiate password reset. Please try again.");
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody @Valid ResetPasswordRequestDto request){
        try {
            userService.resetPassword(request.getToken(), request.getNewPassword());
            return ResponseEntity.ok("Password has been reset successfully.Please Login");
        } catch (RuntimeException | InvalidOrExpiredOtpException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + ex.getMessage());
        }
    }
}
