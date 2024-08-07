package com.tripc.bookingservice.dto;

import com.tripc.bookingservice.enums.BookingStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {

    private Long userId;

    @NotBlank(message = "Name cannot be blank")
    private String name;

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Enter the proper Email Format")
    private String email;

    @NotBlank(message = "Phone number is mandatory")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Phone number should be valid and between 10 to 15 digits")
    private String phoneNumber;

    @NotBlank(message = "Car details cannot be blank")
    private String carDetails;

    @NotBlank(message = "Pickup location cannot be blank")
    private String pickupLocation;

    @NotBlank(message = "Drop location cannot be blank")
    private String dropLocation;

    @NotNull(message = "Pickup date and time cannot be null")
    private LocalDateTime pickupDateTime;

    @NotNull(message = "Total fare cannot be null")
    private Double totalFare;

    private BookingStatus bookingStatus;

    private String paymentOrderId;

    private String paymentStatus;
}
