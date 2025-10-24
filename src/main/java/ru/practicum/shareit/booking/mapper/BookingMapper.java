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

import java.time.LocalDateTime;
import java.time.ZoneId;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookingMapper {

    public static Booking mapToBooking(User booker, Item item, BookingDto requestBookingDto) {
        return Booking.builder()
                .item(item)
                .booker(booker)
                .status(requestBookingDto.getStatus())
                .startDate(LocalDateTime.parse(requestBookingDto.getStart())
                        .atZone(ZoneId.systemDefault())
                        .toInstant())
                .endDate(LocalDateTime.parse(requestBookingDto.getEnd())
                        .atZone(ZoneId.systemDefault())
                        .toInstant())
                .build();
    }

    public static ResponseBookingDto mapToDTO(ItemDto itemDto, Booking booking) {
        return ResponseBookingDto.builder()
                .id(booking.getId())
                .item(itemDto)
                .booker(UserMapper.mapToDTO(booking.getBooker()))
                .status(booking.getStatus())
                .start(LocalDateTime.ofInstant(booking.getStartDate(), ZoneId.systemDefault()).toString())
                .end(LocalDateTime.ofInstant(booking.getEndDate(), ZoneId.systemDefault()).toString())
                .build();
    }
}
