package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;


import java.util.List;

public interface ItemService {
    List<ItemDto> allItems();

    ItemDto itemById(Long itemId);

    ItemDto createItem(Long userId, ItemDto item);

    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    void deleteItem(Long userId, Long itemId);

    List<ItemDto> itemsOfUser(Long userId);

    List<ItemDto> searchItem(String text);
}
