package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ItemMapper {
    public static ItemDto mapToDTO(Item item, List<CommentDTO> comments,
                                   Booking nextBooking, Booking latestBooking) {
        BookingShortDto nextBookingShort = nextBooking != null ? BookingMapper.mapToShortDto(nextBooking) : null;

        BookingShortDto latestBookingShort = latestBooking != null ? BookingMapper.mapToShortDto(latestBooking) : null;

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .comments(comments)
                .nextBooking(nextBookingShort)
                .lastBooking(latestBookingShort)
                .build();
    }

    public static Item mapToItem(User user, ItemDto itemDto) {
        return Item.builder()
                .owner(user)
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemBookingDto mapToItemBookingDTO(Item item) {
        return ItemBookingDto.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }

    public static void updateFields(Item oldItem, Item newItem) {
        oldItem.setName(newItem.getName() == null ? oldItem.getName() : newItem.getName());
        oldItem.setOwner(newItem.getOwner() == null ? oldItem.getOwner() : newItem.getOwner());
        oldItem.setDescription(newItem.getDescription() == null ? oldItem.getDescription() : newItem.getDescription());
        oldItem.setAvailable(newItem.getAvailable() == null ? oldItem.getAvailable() : newItem.getAvailable());
    }
}
