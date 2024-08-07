package com.tripc.paymentservice.controller;

import com.razorpay.RazorpayException;
import com.tripc.paymentservice.dto.PaymentRequestDto;
import com.tripc.paymentservice.dto.PaymentResponseDto;
import com.tripc.paymentservice.dto.PaymentVerificationRequest;
import com.tripc.paymentservice.exception.ResourceNotFoundException;
import com.tripc.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDto paymentRequestDTO) {
        try {
            PaymentResponseDto responseDTO = paymentService.createPayment(paymentRequestDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (RazorpayException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verifyPayment(@RequestBody PaymentVerificationRequest paymentVerificationRequest) {
        try {
            String response = paymentService.verifyPayment(paymentVerificationRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException | ResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
