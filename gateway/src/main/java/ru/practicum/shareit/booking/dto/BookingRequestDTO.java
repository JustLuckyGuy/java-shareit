package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class BookingRequestDTO {
    @NotNull
    @Positive
    private long itemId;
    private StatusBook status;
    @FutureOrPresent
    private LocalDateTime start;
    @Future
    private LocalDateTime end;
}
