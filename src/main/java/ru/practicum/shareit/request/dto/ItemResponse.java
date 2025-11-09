package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemResponse {
    private Long itemId;
    private Long userId;
    private String name;
    private String description;
}
