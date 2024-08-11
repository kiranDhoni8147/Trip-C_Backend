package com.tripc.bookingservice.service;

import com.tripc.bookingservice.dto.BookingRequestDto;
import com.tripc.bookingservice.dto.BookingResponseDto;
import com.tripc.bookingservice.dto.PaymentFailureDto;
import com.tripc.bookingservice.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

public interface BookingService {

    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto);

    public List<BookingResponseDto> getBookingsByUserId(Long userId);

    public BookingResponseDto confirmBooking(String orderId ,String paymentId,String signature) throws ResourceNotFoundException;

    void handlePaymentFailure(PaymentFailureDto paymentFailureDto) throws ResourceNotFoundException;
}
