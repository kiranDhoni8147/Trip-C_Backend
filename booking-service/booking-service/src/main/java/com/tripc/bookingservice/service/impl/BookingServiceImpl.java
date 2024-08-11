package com.tripc.bookingservice.service.impl;

import com.tripc.bookingservice.dto.*;
import com.tripc.bookingservice.enums.BookingStatus;
import com.tripc.bookingservice.exception.ResourceNotFoundException;
import com.tripc.bookingservice.model.Booking;
import com.tripc.bookingservice.repository.BookingRepository;
import com.tripc.bookingservice.service.BookingService;
import com.tripc.bookingservice.service.SendBookingConfirmationNotifications;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    @Autowired
    BookingRepository  bookingRepository;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    WebClient webClient;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    SendBookingConfirmationNotifications sendBookingConfirmationNotifications;

    @Value("${payment.service.url}")
    private String paymentServiceUrl;

    @Override
    public List<BookingResponseDto> getBookingsByUserId(Long userId){
        List<Booking> bookings=bookingRepository.findByUserId(userId);
        return bookings.stream().map(booking-> modelMapper.map(booking, BookingResponseDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public BookingResponseDto createBooking(BookingRequestDto bookingRequestDto) {
        Booking booking = modelMapper.map(bookingRequestDto, Booking.class);
        booking.setBookingStatus(BookingStatus.PENDING);
        Booking savedBooking = bookingRepository.save(booking);

        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
        paymentRequestDto.setBookingId(savedBooking.getId());
        paymentRequestDto.setAmount(savedBooking.getTotalFare());


        try {
            PaymentResponseDto responseDto = webClient.post()
                    .uri(paymentServiceUrl + "/api/payments/create")
                    .bodyValue(paymentRequestDto)
                    .retrieve()
                    .bodyToMono(PaymentResponseDto.class)
                    .block();
            System.out.println(responseDto);
            if (responseDto != null) {
                savedBooking.setPaymentOrderId(responseDto.getOrderId());
                savedBooking.setPaymentStatus(responseDto.getStatus());
                bookingRepository.save(savedBooking);
            } else {
                throw new RuntimeException("Payment initiation failed");
            }
        } catch (Exception e) {
            throw new RuntimeException("An unexpected error occurred during payment initiation", e);
        }

        return modelMapper.map(savedBooking, BookingResponseDto.class);
    }

    @Override
    public BookingResponseDto confirmBooking(String orderId, String paymentId, String signature) throws ResourceNotFoundException {

        Booking booking = bookingRepository.findByPaymentOrderId(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with orderId: " + orderId));

        PaymentVerificationRequest paymentVerificationRequest = new PaymentVerificationRequest(
                paymentId, booking.getPaymentOrderId(), signature);

        try {
            PaymentVerificationResponseDto response= webClient.post()
                    .uri(paymentServiceUrl + "/api/payments/verify")
                    .bodyValue(paymentVerificationRequest)
                    .retrieve()
                    .bodyToMono(PaymentVerificationResponseDto.class)
                    .block();
            System.out.println(response);
            if (response!=null && response.getHttpStatus()==HttpStatus.OK) {
                booking.setBookingStatus(BookingStatus.CONFIRMED);
                booking.setPaymentStatus("CONFIRMED");
                sendBookingConfirmationNotifications.sendBookingConfirmationNotifications(booking);
            } else {
                booking.setBookingStatus(BookingStatus.FAILED);
                booking.setPaymentStatus("FAILED");
            }

        } catch (Exception e) {
            booking.setBookingStatus(BookingStatus.FAILED);
            booking.setPaymentStatus("FAILED");
            e.printStackTrace();
            throw new RuntimeException("An error occurred during payment verification: " + e.getMessage());
        }

        Booking updatedBooking = bookingRepository.save(booking);
        return modelMapper.map(updatedBooking, BookingResponseDto.class);
    }

    @Override
    public void handlePaymentFailure(PaymentFailureDto paymentFailureDto) throws ResourceNotFoundException {
        Booking booking = bookingRepository.findByPaymentOrderId(paymentFailureDto.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with orderId: " + paymentFailureDto.getOrderId()));

        booking.setBookingStatus(BookingStatus.FAILED);
        booking.setPaymentStatus("FAILED");
        bookingRepository.save(booking);
    }

}
