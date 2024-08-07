package com.tripc.bookingservice.service.impl;

import com.tripc.bookingservice.dto.*;
import com.tripc.bookingservice.enums.BookingStatus;
import com.tripc.bookingservice.exception.ResourceNotFoundException;
import com.tripc.bookingservice.model.Booking;
import com.tripc.bookingservice.repository.BookingRepository;
import com.tripc.bookingservice.service.BookingService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    BookingRepository  bookingRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    ModelMapper modelMapper;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) {
        Booking booking = modelMapper.map(bookingRequestDto, Booking.class);
        booking.setBookingStatus(BookingStatus.PENDING);
        Booking savedBooking=bookingRepository.save(booking);

        PaymentRequestDto paymentRequestDto=new PaymentRequestDto();
        paymentRequestDto.setBookingId(savedBooking.getId());
        paymentRequestDto.setAmount(savedBooking.getTotalFare());

        ResponseEntity<PaymentResponseDto> response= restTemplate.postForEntity(paymentServiceUrl+"/api/payments/create",paymentRequestDto, PaymentResponseDto.class);

        if(response.getStatusCode()== HttpStatus.OK){
            PaymentResponseDto responseDto=response.getBody();
            savedBooking.setPaymentOrderId(responseDto.getPaymentId());
            savedBooking.setPaymentStatus(responseDto.getStatus());
            bookingRepository.save(savedBooking);
        }
        else{
            throw new RuntimeException("Payment initiation failed");
        }
        return modelMapper.map(savedBooking,BookingResponseDto.class);
    }

    @Override
    public List<BookingResponseDto> getBookingsByUserId(Long userId){
        List<Booking> bookings=bookingRepository.findByUserId(userId);
        return bookings.stream().map(booking-> modelMapper.map(booking, BookingResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponseDto confirmBooking(Long bookingId,String paymentId,String signature) throws ResourceNotFoundException {

        Booking booking=bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        PaymentVerificationRequest paymentVerificationRequest=new PaymentVerificationRequest(
                paymentId, booking.getPaymentOrderId(),signature);

        ResponseEntity<String> response=restTemplate.postForEntity(paymentServiceUrl+"/api/payments/verify",paymentVerificationRequest,String.class);

        if(response.getStatusCode()== HttpStatus.OK) booking.setBookingStatus(BookingStatus.CONFIRMED);
        else throw new RuntimeException("Payment Verification Failed");

        Booking updatedBooking=bookingRepository.save(booking);
        return modelMapper.map(updatedBooking, BookingResponseDto.class);
    }
}
