package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Builder
@Data
public class ItemDto {
    private String name;
    private String description;
    private User owner;
    private ItemRequest request;
}
