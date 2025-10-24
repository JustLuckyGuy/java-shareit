package ru.practicum.shareit.item.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Transactional(readOnly = true)
public interface ItemService {
    List<ItemDto> allItems();

    ItemDto itemById(Long itemId);

    @Transactional
    ItemDto createItem(Long userId, ItemDto item);

    @Transactional
    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    @Transactional
    void deleteItem(Long userId, Long itemId);

    List<ItemDto> itemsOfUser(Long userId);

    List<ItemDto> searchItem(String text);

    @Transactional
    CommentDTO addComment(long userId, long itemId, CommentDTO commentDto);

    @Transactional
    ItemDto prepareAndMakeItemDto(Item item, boolean initDate);

    @Transactional
    Item prepareAndMakeItemPOJO(long userId, ItemDto itemDto);
}
