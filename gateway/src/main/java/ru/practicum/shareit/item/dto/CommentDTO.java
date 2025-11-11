package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class CommentDTO {
    private long id;
    private String authorName;
    @NotBlank
    private String text;
    private String created;
}
