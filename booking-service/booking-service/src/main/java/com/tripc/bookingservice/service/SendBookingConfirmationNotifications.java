package com.tripc.bookingservice.service;

import com.tripc.bookingservice.model.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class SendBookingConfirmationNotifications{

    @Autowired
    NotificationService notificationService;

    @Autowired
    EmailService emailService;

    public void sendBookingConfirmationNotifications(Booking booking) {
        String ownerMessage = "🚗 **Booking Confirmed!** 🚗\n\n" +
                "**Car Details:** " + booking.getCarDetails() + "\n" +
                "**Pickup Location:** " + booking.getPickUpLocation() + "\n" +
                "**Drop-off Location:** " + booking.getDropLocation() + "\n" +
                "**Pickup Date & Time:** " + booking.getPickupDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")) + "\n\n" +
                "📞 **Customer Contact:** " + booking.getPhoneNumber() + "\n\n" +
                "Please contact the customer to arrange for pickup. Thank you!";

        String ownerPhoneNumber = "7997082352";
        String ownerEmail = "reddi1931@gmail.com";

        String customerMessage = "🎉 **Your Booking is Confirmed!** 🎉\n\n" +
                "**Car Details:** " + booking.getCarDetails() + "\n" +
                "**Pickup Location:** " + booking.getPickUpLocation() + "\n" +
                "**Drop-off Location:** " + booking.getDropLocation() + "\n" +
                "**Pickup Date & Time:** " + booking.getPickupDateTime().format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")) + "\n\n" +
                "📞 **If you have any questions or need to make changes, please contact us at:** " + ownerPhoneNumber + "\n\n" +
                "Thank you for choosing TRIPC! We wish you a pleasant journey.";



        //Sending Messages To The Customer
        notificationService.sendSms(booking.getPhoneNumber(), customerMessage);
        notificationService.sendWhatsAppMessage(booking.getPhoneNumber(), customerMessage);
        emailService.sendEmail(booking.getEmail(), "Booking Confirmation:\n", customerMessage);

        //Sending Messages To The Owner
        notificationService.sendSms(ownerPhoneNumber, "New booking received! " + ownerMessage);
        notificationService.sendWhatsAppMessage(ownerPhoneNumber, "New booking received! " + ownerMessage);
        emailService.sendEmail(ownerEmail, "New Booking Notification", ownerMessage);
    }
}
