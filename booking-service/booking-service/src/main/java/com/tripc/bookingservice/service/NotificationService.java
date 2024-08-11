package com.tripc.bookingservice.service;

public interface NotificationService {
    public void sendSms(String to,String body);
    public void sendWhatsAppMessage(String to, String body);
}
