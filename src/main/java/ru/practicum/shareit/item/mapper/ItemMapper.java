package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {
    public static ItemDto mapToDTO(Item item, List<CommentDTO> comments,
                                   Booking nextBooking, Booking latestBooking) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .bookCount(item.getBookCount())
                .comments(comments)
                .nextBooking(nextBooking != null ? nextBooking.getStartDate().toString() : null)
                .lastBooking(latestBooking != null ? latestBooking.getEndDate().toString() : null)
                .build();
    }

    public static Item mapToItem(User user, ItemDto itemDto) {
        return Item.builder()
                .owner(user)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .bookCount(itemDto.getBookCount() != null ? itemDto.getBookCount() : 0)
                .build();
    }

    public static Item updateFields(Item oldItem, Item newItem) {
        oldItem.setName(newItem.getName() == null ? oldItem.getName() : newItem.getName());
        oldItem.setOwner(newItem.getOwner() == null ? oldItem.getOwner() : newItem.getOwner());
        oldItem.setDescription(newItem.getDescription() == null ? oldItem.getDescription() : newItem.getDescription());
        oldItem.setAvailable(newItem.getAvailable() == null ? oldItem.getAvailable() : newItem.getAvailable());
        return oldItem;
    }
}
