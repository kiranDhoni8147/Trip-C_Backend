package com.tripc.bookingservice.dto;

import lombok.Data;

@Data
public class PaymentResponseDto {

    private String orderId;
    private String status;
}
