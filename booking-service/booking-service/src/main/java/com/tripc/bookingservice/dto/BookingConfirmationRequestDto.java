package com.tripc.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingConfirmationRequestDto {
    private Long bookingId;
    private String paymentId;
    private String signature;

}
