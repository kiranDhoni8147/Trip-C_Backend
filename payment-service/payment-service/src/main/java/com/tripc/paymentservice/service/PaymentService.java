package com.tripc.paymentservice.service;

import com.razorpay.RazorpayException;
import com.tripc.paymentservice.dto.PaymentRequestDto;
import com.tripc.paymentservice.dto.PaymentResponseDto;
import com.tripc.paymentservice.dto.PaymentVerificationRequest;
import com.tripc.paymentservice.dto.PaymentVerificationResponseDto;
import com.tripc.paymentservice.exception.ResourceNotFoundException;

public interface PaymentService {

    public PaymentResponseDto createPayment(PaymentRequestDto requestDto) throws RazorpayException;

    public PaymentVerificationResponseDto verifyPayment(PaymentVerificationRequest verificationRequest) throws ResourceNotFoundException;

    String generateTestSignature(String orderId, String paymentId) throws Exception;

    String verifyTestSignature(String orderId, String paymentId) throws Exception;
}
