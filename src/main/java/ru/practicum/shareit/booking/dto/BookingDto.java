package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.StatusBook;

@Data
@Builder
public class BookingDto {
    @NotNull
    @Positive
    private Long itemId;
    private StatusBook status;
    @NotBlank
    private String start;
    @NotBlank
    private String end;
}
