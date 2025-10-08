package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

/**
 * TODO Sprint add-controllers.
 */
@Data
public class Item {
    private Long id;
    @NotBlank(message = "Поле должно быть заполнено")
    @Size(max = 100, message = "Слишком длинное название")
    private String name;
    private String description;
    private boolean available = false;
    private User owner;
    private ItemRequest request;
}
