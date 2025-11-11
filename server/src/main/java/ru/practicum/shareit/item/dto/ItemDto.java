package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@Builder
@Data
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long request;
    BookingShortDto nextBooking;
    BookingShortDto lastBooking;
    List<CommentDTO> comments;
    private Long requestId;
}

