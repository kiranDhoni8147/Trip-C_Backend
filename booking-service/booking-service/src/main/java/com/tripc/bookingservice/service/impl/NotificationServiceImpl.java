package com.tripc.bookingservice.service.impl;

import com.tripc.bookingservice.service.NotificationService;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {
    @Value("${twilio.account.sid}")
    private String accountSid;

    @Value("${twilio.auth.token}")
    private String authToken;

    @Value("${twilio.phone.number}")
    private String fromPhoneNumber;

    @Value("${twilio.whatsapp.from}")
    private String fromWhatsAppNumber;

    @Override
    public void sendSms(String toPhoneNumber, String body) {
        Twilio.init(accountSid, authToken);
        Message.creator(
                new PhoneNumber("+91"+toPhoneNumber),
                new PhoneNumber(fromPhoneNumber),
                body
        ).create();
    }

    @Override
    public void sendWhatsAppMessage(String to, String body) {
        Twilio.init(accountSid, authToken);
        String whatsappNumber = "whatsapp:+91" + to;
        Message.creator(
                new PhoneNumber(whatsappNumber),
                new PhoneNumber(fromWhatsAppNumber),
                body
        ).create();
    }
}
