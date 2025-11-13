package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CommentDTO {
    private long id;
    private String authorName;
    @NotBlank
    @Size(max = 1000, message = "Слишком длинное описание")
    private String text;
    private String created;
}
