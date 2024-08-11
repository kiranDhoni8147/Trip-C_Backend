package com.tripc.paymentservice.service.impl;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import com.tripc.paymentservice.dto.PaymentRequestDto;
import com.tripc.paymentservice.dto.PaymentResponseDto;
import com.tripc.paymentservice.dto.PaymentVerificationRequest;
import com.tripc.paymentservice.dto.PaymentVerificationResponseDto;
import com.tripc.paymentservice.exception.ResourceNotFoundException;
import com.tripc.paymentservice.model.Payment;
import com.tripc.paymentservice.repository.PaymentRepository;
import com.tripc.paymentservice.service.PaymentService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    PaymentRepository paymentRepository;

    @Autowired
    RazorpayClient razorpayClient;

    @Value("${razorpay.key_secret}")
    private String razorpaySecret;


    @Override
    public PaymentResponseDto createPayment(PaymentRequestDto paymentRequestDto) throws RazorpayException {
        System.out.println("payment service created ");
        JSONObject orderRequest=new JSONObject();
        orderRequest.put("amount",paymentRequestDto.getAmount()*100);
        orderRequest.put("currency","INR");
        orderRequest.put("receipt","receipt_" +paymentRequestDto.getBookingId());


        Order order=razorpayClient.orders.create(orderRequest);

        Payment payment=new Payment();
        payment.setBookingId(paymentRequestDto.getBookingId());
        payment.setOrderId(order.get("id"));
        payment.setStatus("CREATED");

        Payment savedPayment=paymentRepository.save(payment);

        PaymentResponseDto  paymentResponseDto=new PaymentResponseDto();
        paymentResponseDto.setOrderId(savedPayment.getOrderId());
        paymentResponseDto.setStatus(savedPayment.getStatus());
        System.out.println(paymentResponseDto.getOrderId()+" "+paymentResponseDto.getPaymentId()+" "+
                paymentResponseDto.getStatus());
        return paymentResponseDto;
    }

    @Override
    public PaymentVerificationResponseDto verifyPayment(PaymentVerificationRequest paymentVerificationRequest) throws ResourceNotFoundException {
        Payment payment=paymentRepository.findByOrderId(paymentVerificationRequest.getOrderId())
                .orElseThrow(()-> new ResourceNotFoundException("Payment not found with orderId: " + paymentVerificationRequest.getOrderId()));
        System.out.println(payment.getStatus()+" reached verifypayment endpoint");
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
            return new PaymentVerificationResponseDto("Payment verified successfully", HttpStatus.OK);
        } else {
            return new PaymentVerificationResponseDto("Payment verification failed", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public String generateTestSignature(String orderId, String paymentId) throws Exception {
        String secret = razorpaySecret;  // Already configured in your service
        String data = orderId + "|" + paymentId;
        Mac sha256HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
        sha256HMAC.init(secretKey);
        return Base64.getEncoder().encodeToString(sha256HMAC.doFinal(data.getBytes()));
    }

    @Override
    public String verifyTestSignature(String orderId, String paymentId) throws Exception {
        return generateTestSignature(orderId, paymentId);
    }

}
