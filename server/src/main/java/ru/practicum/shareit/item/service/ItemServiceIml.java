package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
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
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;


import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceIml implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;

    @Override
    public List<ItemDto> allItems() {
        List<Item> items = itemRepository.findAll();

        if (items.isEmpty()) {
            return List.of();
        }

        List<Comment> allComments = commentRepository.findByItemIn(items, Sort.by(Sort.Direction.DESC, "created"));
        Map<Item, List<Comment>> commentsByItem = allComments.stream()
                .collect(groupingBy(Comment::getItem));

        Map<Item, List<Booking>> bookingsMap = bookingRepository.findApprovedForItems(items, Sort.by(Sort.Direction.DESC, "startDate"))
                .stream()
                .collect(groupingBy(Booking::getItem, toList()));

        return items.stream()
                .map(item -> prepareAndMakeItemDto(item, bookingsMap, commentsByItem, false))
                .toList();
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
        if (itemDto.getRequestId() != null) {
            ItemRequest request = requestRepository.findById(itemDto.getRequestId()).orElseThrow(() ->
                    new NotFoundException("Запрос с таким id: " + itemDto.getRequestId() + " не найден"));

            request.getItems().add(item.getId());
            requestRepository.save(request);
        }

        return prepareAndMakeItemDto(item, false);
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        if (!itemRepository.existsByIdAndOwnerId(itemId, userId)) {
            throw new ConditionsNotMatchException("Только владелец может изменять данные предмета");
        }
        Item olditem = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Предмет не найден"));
        ItemMapper.updateFields(olditem, prepareAndMakeItemPOJO(userId, itemDto));
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
        final List<Booking> bookings = bookingRepository.findByItemIdAndBookerId(itemId, userId);
        final LocalDateTime now = LocalDateTime.now();
        if (bookings.stream().noneMatch(b -> b.isFinished(now))) {
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


    private ItemDto prepareAndMakeItemDto(Item item, boolean initDate) {
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


    public Item prepareAndMakeItemPOJO(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id '" + userId + "' не найден"));

        return ItemMapper.mapToItem(user, itemDto);
    }

    private ItemDto prepareAndMakeItemDto(Item item, Map<Item, List<Booking>> bookingsByItem,
                                          Map<Item, List<Comment>> commentsByItem, boolean initDate) {

        List<Booking> itemBookings = bookingsByItem.getOrDefault(item, List.of());

        List<Comment> itemComments = commentsByItem.getOrDefault(item, List.of());
        List<CommentDTO> commentDTOs = itemComments.stream()
                .map(CommentMapper::mapToDTO)
                .toList();

        Booking latestBooking = null;
        Booking nextBooking = null;

        if (initDate && !itemBookings.isEmpty()) {
            latestBooking = findLastBooking(itemBookings);
            nextBooking = findNextBooking(itemBookings);
        }
        return ItemMapper.mapToDTO(item, commentDTOs, nextBooking, latestBooking);
    }

    private Booking findNextBooking(List<Booking> bookings) {
        LocalDateTime now = LocalDateTime.now();
        return bookings.stream()
                .filter(booking -> booking.getStartDate().isAfter(now))
                .min(Comparator.comparing(Booking::getStartDate))
                .orElse(null);
    }

    private Booking findLastBooking(List<Booking> bookings) {
        LocalDateTime now = LocalDateTime.now();
        return bookings.stream()
                .filter(booking -> booking.getEndDate().isBefore(now))
                .max(Comparator.comparing(Booking::getEndDate))
                .orElse(null);
    }
}