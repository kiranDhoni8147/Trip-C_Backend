package com.tripc.bookingservice.dto;

import lombok.Data;

@Data
public class PaymentResponseDto {

    private String paymentId;
    private String status;
}
