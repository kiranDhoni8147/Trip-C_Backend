package com.tripc.userservice.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingResponseDto {
    private Long id;
    private Long userId;
    private String name;
    private String email;
    private String phoneNumber;
    private String carDetails;
    private String pickupLocation;
    private String dropLocation;
    private LocalDateTime pickupDateTime;
    private BigDecimal totalFare;
    private BookingStatus bookingStatus;
    private String orderId;
    private String paymentStatus;
}
