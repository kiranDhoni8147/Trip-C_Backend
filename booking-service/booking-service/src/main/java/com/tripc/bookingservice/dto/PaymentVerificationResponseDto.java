package com.tripc.bookingservice.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentVerificationResponseDto {
    private String message;
    private HttpStatus httpStatus;
}
