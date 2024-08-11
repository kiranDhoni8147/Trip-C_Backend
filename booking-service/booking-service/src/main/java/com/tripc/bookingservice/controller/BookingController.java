package com.tripc.bookingservice.controller;

import com.tripc.bookingservice.dto.BookingConfirmationRequestDto;
import com.tripc.bookingservice.dto.BookingRequestDto;
import com.tripc.bookingservice.dto.BookingResponseDto;
import com.tripc.bookingservice.dto.PaymentFailureDto;
import com.tripc.bookingservice.exception.ResourceNotFoundException;
import com.tripc.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/bookings")
public class BookingController {

    @Autowired
    BookingService bookingService;

    @PostMapping("/createbooking")
    public ResponseEntity<BookingResponseDto> createBooking(@RequestBody @Valid BookingRequestDto requestDto, @AuthenticationPrincipal UserDetails userDetails){
        requestDto.setUserId(Long.valueOf(userDetails.getUsername()));
        BookingResponseDto responseDto=bookingService.createBooking(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @PostMapping("/confirmbooking")
    public ResponseEntity<BookingResponseDto> confirmBooking(@RequestBody BookingConfirmationRequestDto requestDto) throws ResourceNotFoundException {
        BookingResponseDto responseDto=bookingService.confirmBooking(
                requestDto.getOrderId(),
                requestDto.getPaymentId(),
                requestDto.getSignature()
        );

        return new ResponseEntity<>(responseDto,HttpStatus.OK);
    }

    @PostMapping("/payment-failed")
    public ResponseEntity<String> handlePaymentFailure(@RequestBody PaymentFailureDto paymentFailureDto) {
        try {
            // Update booking status to FAILED based on the payment failure information
            System.out.println("request reached to update payment failure in database");
            bookingService.handlePaymentFailure(paymentFailureDto);
            return ResponseEntity.ok("Payment failure recorded");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to record payment failure");
        }
    }


    @GetMapping("/getbookingsbyuserid")
    public ResponseEntity<List<BookingResponseDto>> getBookingsByUserId(@RequestParam Long userId) {
        System.out.println("request reached to booking controller");
        List<BookingResponseDto> bookings = bookingService.getBookingsByUserId(userId);
        if (bookings != null && !bookings.isEmpty()) {
            return ResponseEntity.ok(bookings);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
