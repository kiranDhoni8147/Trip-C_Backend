package com.tripc.bookingservice.controller;

import com.tripc.bookingservice.dto.BookingConfirmationRequestDto;
import com.tripc.bookingservice.dto.BookingRequestDto;
import com.tripc.bookingservice.dto.BookingResponseDto;
import com.tripc.bookingservice.exception.ResourceNotFoundException;
import com.tripc.bookingservice.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/message")
    public String getMessage(){
        return "Got A Message";
    }

    @PostMapping("/confirmbooking")
    public ResponseEntity<BookingResponseDto> confirmBooking(@RequestBody BookingConfirmationRequestDto requestDto) throws ResourceNotFoundException {
        BookingResponseDto responseDto=bookingService.confirmBooking(
                requestDto.getBookingId(),
                requestDto.getPaymentId(),
                requestDto.getSignature()
        );

        return new ResponseEntity<>(responseDto,HttpStatus.OK);

    }
}
