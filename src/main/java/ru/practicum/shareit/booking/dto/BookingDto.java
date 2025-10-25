package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.StatusBook;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    @NotNull
    @Positive
    private Long itemId;
    private StatusBook status;
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
}
