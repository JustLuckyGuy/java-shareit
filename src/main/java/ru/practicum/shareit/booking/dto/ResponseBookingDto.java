package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.StatusBook;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.user.dto.UserDTO;

import java.time.LocalDateTime;

@Data
@Builder
public class ResponseBookingDto {
    private Long id;
    private ItemBookingDto item;
    private UserDTO booker;
    private StatusBook status;
    private LocalDateTime start;
    private LocalDateTime end;
}
