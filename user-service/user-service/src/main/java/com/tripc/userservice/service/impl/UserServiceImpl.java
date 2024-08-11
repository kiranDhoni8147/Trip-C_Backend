package com.tripc.userservice.service.impl;

import com.tripc.userservice.dto.BookingResponseDto;
import com.tripc.userservice.dto.ContactRequestDto;
import com.tripc.userservice.dto.LoginRequestDto;
import com.tripc.userservice.dto.UserDto;
import com.tripc.userservice.exception.InvalidOrExpiredOtpException;
import com.tripc.userservice.exception.LoginException;
import com.tripc.userservice.model.PasswordResetToken;
import com.tripc.userservice.model.User;
import com.tripc.userservice.repository.PasswordResetTokenRepository;
import com.tripc.userservice.repository.UserRepository;
import com.tripc.userservice.service.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository  userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    JwtService jwtService;

    @Autowired
    TokenService tokenService;

    @Autowired
    MailService mailService;

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    SmsService smsService;

    @Autowired
    WhatsappService whatsappService;

    @Autowired
    RestTemplate restTemplate;

    private final String bookingServiceUrl = "http://localhost:8082/api/bookings/getbookingsbyuserid";

    @Override
    public void register(UserDto userDto) {
        Optional<User> existingUser= userRepository.findByPhoneNumber(userDto.getPhoneNumber());
        if(existingUser.isPresent()){
            throw new RuntimeException("Phone Number Already Registered,Please Login");
        }
        User user=modelMapper.map(userDto,User.class);
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userRepository.save(user);
    }

    @Override
    public String login(LoginRequestDto loginRequestDto) throws LoginException {

        User user = userRepository.findByPhoneNumber(loginRequestDto.getPhoneNumber())
                .orElseThrow(() -> new LoginException("Invalid Phone Number"));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new LoginException("Invalid Password");
        }
        return jwtService.generateToken(user.getPhoneNumber());
    }

    @Override
    public UserDto getUserByPhoneNumber(String phoneNumber) {
        User user= userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User not found with MobileNumber: "+phoneNumber));
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public void forgotPassword(String phoneNumber) {
        User user=userRepository.findByPhoneNumber(phoneNumber).get();
        if(user==null)throw new RuntimeException("User not found with phone number: " + phoneNumber);

        String otp=tokenService.createToken(user);

        whatsappService.sendPasswordResetWhatsApp(user,otp);
        smsService.sendPasswordResetSMS(user,otp);
        mailService.sendPasswordResetToken(user,otp);
    }

    @Override
    public void resetPassword(String otp, String newPassword) throws InvalidOrExpiredOtpException {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(otp);
        if (passwordResetToken == null || passwordResetToken.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new InvalidOrExpiredOtpException("Invalid or expired OTP");
        }

        User user = passwordResetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        passwordResetTokenRepository.delete(passwordResetToken);

        smsService.sendSuccessSMS(user.getPhoneNumber());
        whatsappService.sendSuccessWhatsApp(user.getPhoneNumber());
        mailService.sendSuccessMail(user.getEmail());
    }

    @Override
    public List<BookingResponseDto> getBookingsByUserId(String userId,String token) {

        String url = bookingServiceUrl + "?userId=" + userId;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token); // Sets Authorization header with Bearer token

        HttpEntity<String> entity = new HttpEntity<>(headers);
        ResponseEntity<List<BookingResponseDto>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                new ParameterizedTypeReference<List<BookingResponseDto>>() {}
        );
        return response.getBody();
    }

    @Override
    public void sendContactEmail(ContactRequestDto contactRequestDto) {
        mailService.sendContactUsMail(contactRequestDto);
    }
}
