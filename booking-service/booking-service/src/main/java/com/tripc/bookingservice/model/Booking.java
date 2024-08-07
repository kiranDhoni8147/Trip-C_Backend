package com.tripc.bookingservice.model;

import com.tripc.bookingservice.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    private String name;

    private String email;

    private String phoneNumber;

    private String carDetails;

    private String pickUpLocation;

    private String dropLocation;

    private LocalDateTime pickupDateTime;

    private LocalDateTime bookingTime;

    private Double totalFare;

    private BookingStatus bookingStatus;

    private String paymentOrderId;

    private String paymentStatus;

}
