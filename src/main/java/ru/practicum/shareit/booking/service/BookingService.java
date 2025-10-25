package ru.practicum.shareit.booking.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;

import java.util.List;

@Service
public interface BookingService {
    ResponseBookingDto bookItem(long userId, BookingDto requestBookingDto);

    ResponseBookingDto changeBookStatus(long ownerId, long bookingId, boolean approved);

    ResponseBookingDto getBooking(long bookingId);

    List<ResponseBookingDto> getUserBookings(Long userId, String state);

    List<ResponseBookingDto> getOwnerBookings(Long ownerId, String state);

}
