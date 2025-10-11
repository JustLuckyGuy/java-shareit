package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;


import java.util.*;


@Repository
public class ItemRepositoryIml implements ItemRepository{
    private final Comparator<Long> comparator = Comparator.comparing(ob -> ob);
    private final HashMap<Long, Item> items = new HashMap<>();

    @Override
    public Collection<Item> findAllItems() {
        return items.values();
    }

    @Override
    public Item findItemById(Long itemId) {
        isItemExist(itemId);
        return items.get(itemId);
    }

    @Override
    public Item save(Item item) {
        long id = items.values().stream()
                .map(Item::getId)
                .max(comparator)
                .orElse((long) 0) + 1;

        item.setId(id);
        items.put(id, item);
        return item;
    }

    @Override
    public Item update(Long itemId, Item item) {
        isItemExist(itemId);
        Item existingItem = items.get(itemId);
        existingItem.setName(item.getName() == null ? existingItem.getName() : item.getName());
        existingItem.setDescription(item.getDescription() == null ? existingItem.getDescription() : item.getDescription());
        existingItem.setOwner(item.getOwner() == null ? existingItem.getOwner() : item.getOwner());
        existingItem.setRequest(item.getRequest() == null ? existingItem.getRequest() : item.getRequest());
        existingItem.setAvailable(item.getAvailable() == null ? existingItem.getAvailable() : item.getAvailable());
        items.put(itemId, existingItem);
        return items.get(itemId);
    }

    @Override
    public void delete(Long itemId) {
        isItemExist(itemId);
        items.remove(itemId);
    }

    @Override
    public List<Item> getUserItems(long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == userId)
                .toList();
    }

    @Override
    public List<Item> itemSearch(String text) {
        return items.values().stream()
                .filter(item -> !(text.isBlank()) && item.getAvailable())
                .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()))
                .toList();
    }

    @Override
    public boolean checkOwner(Long userId, Long itemId){
        if (items.containsKey(itemId)) {
            return Objects.equals(items.get(itemId).getOwner(), userId);
        } else {
            throw new NotFoundException("Не найден предмет с id: " + itemId);
        }
    }

    private void isItemExist(Long itemId){
        if(!items.containsKey(itemId)){
            throw new NotFoundException("Пользователь с id: "+ itemId +" не найден");
        }
    }





}
