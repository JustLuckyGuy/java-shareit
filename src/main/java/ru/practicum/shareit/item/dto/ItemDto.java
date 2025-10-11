package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ItemDto {
    @Positive
    private Long id;
    @NotBlank(message = "Поле должно быть заполнено")
    @Size(max = 100, message = "Слишком длинное название")
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Long request;
}
