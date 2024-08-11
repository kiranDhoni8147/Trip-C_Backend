package com.tripc.userservice.controller;

import com.tripc.userservice.dto.*;
import com.tripc.userservice.exception.InvalidOrExpiredOtpException;
import com.tripc.userservice.exception.LoginException;
import com.tripc.userservice.model.User;
import com.tripc.userservice.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

    @GetMapping("/{phoneNumber}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String phoneNumber) {
        UserDto userDto = userService.getUserByPhoneNumber(phoneNumber);
        if (userDto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // Return 404 if user is not found
        }
        return new ResponseEntity<>(userDto, HttpStatus.OK);
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

    @GetMapping("/contact-us")
    public ResponseEntity<String> submitContactForm(@RequestBody @Valid ContactRequestDto contactRequestDto) {
        userService.sendContactEmail(contactRequestDto);
        return ResponseEntity.ok("Your message has been sent successfully.");
    }

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingResponseDto>> getBookings(Authentication authentication, HttpServletRequest request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String userId = authentication.getName();
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7); // Remove "Bearer " from the token string
        }

        if (userId == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // User ID not found
        }
        List<BookingResponseDto> bookings = userService.getBookingsByUserId(userId,token);

        if (bookings != null) {
            return ResponseEntity.ok(bookings);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); // No bookings found
        }
    }
}
