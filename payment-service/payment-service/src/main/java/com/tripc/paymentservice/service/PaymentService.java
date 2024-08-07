package com.tripc.paymentservice.service;

import com.razorpay.RazorpayException;
import com.tripc.paymentservice.dto.PaymentRequestDto;
import com.tripc.paymentservice.dto.PaymentResponseDto;
import com.tripc.paymentservice.dto.PaymentVerificationRequest;
import com.tripc.paymentservice.exception.ResourceNotFoundException;

public interface PaymentService {

    public PaymentResponseDto createPayment(PaymentRequestDto requestDto) throws RazorpayException;

    public String verifyPayment(PaymentVerificationRequest verificationRequest) throws ResourceNotFoundException;
}
