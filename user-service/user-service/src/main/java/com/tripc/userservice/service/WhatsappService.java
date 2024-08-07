package com.tripc.userservice.service;

import com.tripc.userservice.model.User;

public interface WhatsappService {

    void sendPasswordResetWhatsApp(User user, String otp);
    void sendSuccessWhatsApp(String phoneNumber);
}
