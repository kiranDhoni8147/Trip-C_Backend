package com.tripc.userservice.service;

import com.tripc.userservice.dto.ContactRequestDto;
import com.tripc.userservice.model.User;

public interface MailService {

    public void sendPasswordResetToken(User user, String token);

    void sendSuccessMail(String email);

    void sendContactUsMail(ContactRequestDto contactRequestDto);
}
