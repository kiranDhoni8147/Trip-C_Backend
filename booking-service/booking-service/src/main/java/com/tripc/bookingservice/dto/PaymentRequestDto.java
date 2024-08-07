package com.tripc.bookingservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
public class PaymentRequestDto {
    private Long bookingId;
    private Double amount;

}
