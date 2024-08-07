package com.tripc.userservice.service.impl;

import com.tripc.userservice.model.PasswordResetToken;
import com.tripc.userservice.model.User;
import com.tripc.userservice.repository.PasswordResetTokenRepository;
import com.tripc.userservice.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class TokenServiceImpl implements TokenService {

    @Autowired
    PasswordResetTokenRepository passwordResetTokenRepository;

    @Override
    public String createToken(User user) {
        Optional<PasswordResetToken> existingToken = passwordResetTokenRepository.findByUser(user);

        // If a token already exists, delete it
        existingToken.ifPresent(passwordResetTokenRepository::delete);

        // Create a new token
        String otp = generateOtp();
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(otp);
        passwordResetToken.setUser(user);
        passwordResetToken.setExpirationTime(LocalDateTime.now().plusMinutes(10));

        passwordResetTokenRepository.save(passwordResetToken);

        return otp;
    }

    private String generateOtp() {
        Random random=new Random();
        int otp=100000+random.nextInt(900000);
        return String.valueOf(otp);
    }
}
