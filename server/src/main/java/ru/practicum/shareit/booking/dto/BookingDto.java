package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.StatusBook;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long itemId;
    private StatusBook status;
    private LocalDateTime start;
    private LocalDateTime end;
}
