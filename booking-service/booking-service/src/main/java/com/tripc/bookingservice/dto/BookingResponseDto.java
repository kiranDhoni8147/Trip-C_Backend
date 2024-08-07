package com.tripc.bookingservice.dto;

import com.tripc.bookingservice.enums.BookingStatus;
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
    private BookingStatus status;
    private String paymentId;
    private String paymentStatus;
}
