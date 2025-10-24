package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.StatusBook;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDTO;

@Data
@Builder
public class ResponseBookingDto {
    private Long id;
    private ItemDto item;
    private UserDTO booker;
    private StatusBook status;
    private String start;
    private String end;
}
