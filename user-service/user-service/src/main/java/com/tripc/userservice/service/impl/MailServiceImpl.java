package com.tripc.userservice.service.impl;

import com.tripc.userservice.dto.ContactRequestDto;
import com.tripc.userservice.model.User;
import com.tripc.userservice.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;



    @Override
    public void sendPasswordResetToken(User user, String otp) {

        String subject = "Password Reset Request";
        String message = "Dear " + user.getUserName() + ",\n\n" +
                "You have requested to reset your password. Please use the following OTP to reset your password:\n" +
                 otp + "\n\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Best regards,\n" +
                "TRIPC";

        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setTo(user.getEmail());
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        javaMailSender.send(simpleMailMessage);
    }

    @Override
    public void sendSuccessMail(String email) {
        String subject = "Password Reset Successful";
        String message = "Dear User,\n\n" +
                "Your password has been successfully reset.Please log in to enjoy TRIPC. \n\n If you did not request this change, please contact our support team immediately.\n\n" +
                "Best regards,\n" +
                "TRIPC";

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(message);

        javaMailSender.send(simpleMailMessage);
    }

    @Override
    public void sendContactUsMail(ContactRequestDto contactRequestDto) {
        SimpleMailMessage simpleMailMessage=new SimpleMailMessage();
        simpleMailMessage.setTo(contactRequestDto.getEmail());
        simpleMailMessage.setSubject("New Contact Us Query from " + contactRequestDto.getName());
        simpleMailMessage.setText("Message: " + contactRequestDto.getMessage() + "\n\n"
                + "From: " + contactRequestDto.getName() + "\n"
                + "Email: " + contactRequestDto.getEmail() + "\n"
                + "Subject: " + contactRequestDto.getSubject());

        javaMailSender.send(simpleMailMessage);
    }


}
