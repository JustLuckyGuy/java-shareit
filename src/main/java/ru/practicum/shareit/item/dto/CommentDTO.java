package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDTO {
    @Positive
    private long id;
    private String authorName;
    private String text;
    private String created;
}