package com.tripc.userservice.service;


import com.tripc.userservice.model.User;

public interface SmsService {
    void sendPasswordResetSMS(User user, String otp);
    void sendSuccessSMS(String phoneNumber);
}
