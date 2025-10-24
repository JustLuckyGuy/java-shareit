package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBook;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.ConditionsNotMatchException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceIml implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public List<ItemDto> allItems() {
        return itemRepository.findAll().stream().map(item -> prepareAndMakeItemDto(item, false)).toList();
    }

    @Override
    public ItemDto itemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id '" + itemId + "' не найден"));

        bookingRepository.existsByItemId(itemId);

        return prepareAndMakeItemDto(item, false);
    }

    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        Item item = itemRepository.save(prepareAndMakeItemPOJO(userId, itemDto));
        return prepareAndMakeItemDto(item, false);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        if (!itemRepository.existsByIdAndOwnerId(itemId, userId)) {
            throw new ConditionsNotMatchException("Только владелец может изменять данные предмета");
        }
        Item olditem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        olditem = ItemMapper.updateFields(olditem, prepareAndMakeItemPOJO(userId, itemDto));
        return prepareAndMakeItemDto(olditem, false);
    }

    @Override
    public void deleteItem(Long userId, Long itemId) {
        if (!itemRepository.existsByIdAndOwnerId(itemId, userId)) {
            throw new ConditionsNotMatchException("Только владелец может изменять данные предмета");
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    public List<ItemDto> itemsOfUser(Long userId) {
        userRepository.existsById(userId);
        return itemRepository.findByOwnerId(userId).stream()
                .map(item -> prepareAndMakeItemDto(item, true))
                .toList();
    }

    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return List.of();
        }
        return itemRepository.findByNameContainingOrDescriptionContainingAndAvailableTrue(text.toLowerCase()).stream()
                .map(item -> prepareAndMakeItemDto(item, false))
                .toList();
    }

    @Override
    public CommentDTO addComment(long userId, long itemId, CommentDTO commentDto) {
        if (!bookingRepository.existsByBookerIdAndItemIdAndEndBefore(userId, itemId, StatusBook.APPROVED)) {
            throw new BadRequestException("Чтобы оставить отзыв на предмет," +
                    " нужно воспользоваться им");
        }

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Предмет с id '" + itemId + "' не найден"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id '" + userId + "' не найден"));

        commentDto.setId(itemId);
        commentDto.setAuthorName(user.getName());
        Comment comment = CommentMapper.mapToComment(item, commentDto);

        return CommentMapper.mapToDTO(commentRepository.save(comment));
    }

    @Override
    public ItemDto prepareAndMakeItemDto(Item item, boolean initDate) {
        Booking latestBooking = null;
        Booking nextBooking = null;

        if (bookingRepository.existsByItemId(item.getId()) && initDate) {
            latestBooking = bookingRepository.getNearliestPastBooking(item.getId());
            nextBooking = bookingRepository.getNearliestFutureBooking(item.getId());
        }

        List<CommentDTO> comments = commentRepository.findByItemId(item.getId()).stream()
                .map(CommentMapper::mapToDTO)
                .toList();

        return ItemMapper.mapToDTO(item, comments, nextBooking, latestBooking);
    }

    @Override
    public Item prepareAndMakeItemPOJO(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id '" + userId + "' не найден"));

        return ItemMapper.mapToItem(user, itemDto);
    }
}