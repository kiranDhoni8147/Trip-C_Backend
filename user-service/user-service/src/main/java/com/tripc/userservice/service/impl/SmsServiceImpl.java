package com.tripc.userservice.service.impl;

import com.tripc.userservice.model.User;
import com.tripc.userservice.service.SmsService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {

    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;


    @Override
    public void sendPasswordResetSMS(User user, String otp){
        Twilio.init(accountSid,authToken);

        Message.creator(
                new PhoneNumber("+91"+user.getPhoneNumber()),
                new PhoneNumber(fromPhoneNumber),
                "Dear " + user.getUserName() + ",\n\n" +
                        "You have requested to reset your password. Please use the following OTP to reset your password:\n" +
                        otp + "\n\n" +
                        "If you did not request this, please ignore this email.\n\n" +
                        "Best regards,\n" +
                        "TRIPC"
        ).create();

    }

    @Override
    public void sendSuccessSMS(String phoneNumber) {
        Twilio.init(accountSid, authToken);
        Message.creator(
                        new PhoneNumber("+91"+phoneNumber),
                        new PhoneNumber(fromPhoneNumber),
                        "Dear User, your password has been successfully reset.Please log in to enjoy TRIPC\n\n If you did not request this change, please contact our support team immediately. \n\n Thank you for using TRIPC.")
                .create();
    }
}
