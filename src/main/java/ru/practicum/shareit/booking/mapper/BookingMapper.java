package ru.practicum.shareit.booking.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.ResponseBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {

    public static Booking mapToBooking(User booker, Item item, BookingDto requestBookingDto) {
        return Booking.builder()
                .item(item)
                .booker(booker)
                .status(requestBookingDto.getStatus())
                .startDate(requestBookingDto.getStart())
                .endDate(requestBookingDto.getEnd())
                .build();
    }

    public static ResponseBookingDto mapToDTO(ItemDto itemDto, Booking booking) {
        return ResponseBookingDto.builder()
                .id(booking.getId())
                .item(itemDto)
                .booker(UserMapper.mapToDTO(booking.getBooker()))
                .status(booking.getStatus())
                .start(booking.getStartDate())
                .end(booking.getEndDate())
                .build();
    }
}
