package com.tripc.paymentservice.controller;

import com.razorpay.RazorpayException;
import com.tripc.paymentservice.dto.PaymentRequestDto;
import com.tripc.paymentservice.dto.PaymentResponseDto;
import com.tripc.paymentservice.dto.PaymentVerificationRequest;
import com.tripc.paymentservice.dto.PaymentVerificationResponseDto;
import com.tripc.paymentservice.exception.ResourceNotFoundException;
import com.tripc.paymentservice.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    PaymentService paymentService;

    @PostMapping("/create")
    public ResponseEntity<PaymentResponseDto> createPayment(@RequestBody PaymentRequestDto paymentRequestDTO) {
        try {
            System.out.println("request reached to payment controller to create payment");
            PaymentResponseDto responseDTO = paymentService.createPayment(paymentRequestDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
        } catch (RazorpayException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<PaymentVerificationResponseDto> verifyPayment(@RequestBody PaymentVerificationRequest paymentVerificationRequest) {
        try {
            PaymentVerificationResponseDto response = paymentService.verifyPayment(paymentVerificationRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (RuntimeException | ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new PaymentVerificationResponseDto("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }
    @GetMapping("/test-signature")
    public ResponseEntity<String> getTestSignature(@RequestParam String orderId, @RequestParam String paymentId) {
        try {
            String testSignature = paymentService.verifyTestSignature(orderId, paymentId);
            return new ResponseEntity<>(testSignature, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error generating signature", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
