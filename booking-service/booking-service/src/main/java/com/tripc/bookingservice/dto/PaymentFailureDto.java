package com.tripc.bookingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentFailureDto {
    private String orderId;
    private String paymentId;
    private String reason;
    private String signature;

}
