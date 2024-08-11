package com.tripc.bookingservice.repository;

import com.tripc.bookingservice.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking,Long> {

    List<Booking> findByUserId(Long userId);

    Optional<Booking> findByPaymentOrderId(String orderId);
}
