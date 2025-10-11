package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConditionsNotMatchException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceIml implements ItemService{
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<ItemDto> allItems() {
        log.info("Пользователи: {}", userRepository.findAllUsers() );
        return itemRepository.findAllItems().stream().map(ItemMapper::mapToDTO).toList();
    }

    @Override
    public ItemDto itemById(Long itemId) {
        return ItemMapper.mapToDTO(itemRepository.findItemById(itemId));
    }

    @Override
    public List<ItemDto> itemsOfUser(Long userId){
        checkUser(userId);
        return itemRepository.getUserItems(userId).stream()
                .map(ItemMapper::mapToDTO)
                .toList();
    }

    @Override
    public List<ItemDto> searchItem(String text){
        return itemRepository.itemSearch(text).stream()
                .map(ItemMapper::mapToDTO)
                .toList();
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto item) {
        checkUser(userId);
        Item newItem = ItemMapper.mapToItem(userId, item);
        return ItemMapper.mapToDTO(itemRepository.save(newItem));
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto item) {
        checkUser(userId);
        if (itemRepository.checkOwner(userId, itemId)) {
            Item updatedItem = ItemMapper.mapToItem(userId, item);
            return ItemMapper.mapToDTO(itemRepository.update(itemId, updatedItem));
        } else {
            throw new ConditionsNotMatchException("Только владелец может изменять данные предмета");
        }
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        checkUser(userId);
        if (itemRepository.checkOwner(userId, itemId)) {
            itemRepository.delete(itemId);
        } else {
            throw new ConditionsNotMatchException("Только владелец может удалить предмет");
        }
    }

    private void checkUser(Long userId){
        if(userRepository.findUserById(userId) == null){
            throw new NotFoundException("Данного пользователя не существует");
        }
    }

}
