package ru.practicum.shareit.item.repository;


import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemRepository {
    Collection<Item> findAllItems();

    Item findItemById(Long itemId);

    Item save(Item user);

    Item update(Long itemId, Item item);

    void delete(Long itemId);

    boolean checkOwner(Long userId, Long itemId);

    List<Item> getUserItems(long userId);

    List<Item> itemSearch(String text);
}
