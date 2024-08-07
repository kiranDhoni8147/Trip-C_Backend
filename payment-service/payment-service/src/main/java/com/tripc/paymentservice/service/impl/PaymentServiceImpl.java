package com.tripc.paymentservice.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.tripc.paymentservice.dto.PaymentRequestDto;
import com.tripc.paymentservice.dto.PaymentResponseDto;
import com.tripc.paymentservice.dto.PaymentVerificationRequest;
import com.tripc.paymentservice.exception.ResourceNotFoundException;
import com.tripc.paymentservice.model.Payment;
import com.tripc.paymentservice.repository.PaymentRepository;
import com.tripc.paymentservice.service.PaymentService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class PaymentServiceImpl implements PaymentService {
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    RazorpayClient razorpayClient;

    @Value("${razorpay.key_secret}")
    private String razorpaySecret;


    @Override
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) throws RazorpayException {

        JSONObject orderRequest=new JSONObject();
        orderRequest.put("amount",paymentRequestDto.getAmount());
        orderRequest.put("currency","INR");
        orderRequest.put("receipt","receipt_" +paymentRequestDto.getBookingId());


        Order order=razorpayClient.orders.create(orderRequest);

        Payment payment=new Payment();
        payment.setBookingId(payment.getBookingId());
        payment.setPaymentId(order.get("id"));
        payment.setStatus("CREATED");

        Payment savedPayment=paymentRepository.save(payment);

        PaymentResponseDto  paymentResponseDto=new PaymentResponseDto();
        paymentResponseDto.setOrderId(savedPayment.getOrderId());
        paymentResponseDto.setStatus("CREATED");

        return paymentResponseDto;
    }

    @Override
    public String verifyPayment(PaymentVerificationRequest paymentVerificationRequest) throws ResourceNotFoundException {
        Payment payment=paymentRepository.findByOrderId(paymentVerificationRequest.getOrderId())
                .orElseThrow(()-> new ResourceNotFoundException("Payment not found with orderId: " + paymentVerificationRequest.getOrderId()));

        JSONObject options = new JSONObject();
        options.put("razorpay_payment_id", paymentVerificationRequest.getPaymentId());
        options.put("razorpay_order_id", paymentVerificationRequest.getOrderId());
        options.put("razorpay_signature", paymentVerificationRequest.getSignature());

        boolean isValidSignature;
        try {
            isValidSignature = Utils.verifyPaymentSignature(options,razorpaySecret);
        } catch (RazorpayException e) {
            throw new RuntimeException("Payment verification failed", e);
        }

        if (isValidSignature) {
            payment.setStatus("CONFIRMED");
            payment.setPaymentId(paymentVerificationRequest.getPaymentId());
            paymentRepository.save(payment);
            return "Payment verified successfully";
        } else {
            throw new RuntimeException("Payment verification failed");
        }
    }
}
