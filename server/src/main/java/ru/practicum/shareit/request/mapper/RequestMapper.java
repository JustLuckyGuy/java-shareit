package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class RequestMapper {
    private final ItemRepository repository;

    public ItemRequestDto mapToDto(ItemRequest request) {
        List<ItemResponse> items = new ArrayList<>();
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            items = request.getItems().stream()
                    .map(id -> {
                        Item item = repository.findById(id).
                                orElseThrow(() -> new NotFoundException("Предмет с id: " + id + " не найден"));
                        return ItemResponse.builder()
                                .itemId(item.getId())
                                .userId(item.getOwner().getId())
                                .name(item.getName())
                                .description(item.getDescription())
                                .build();
                    })
                    .toList();
        }

        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .items(items)
                .created(request.getCreatedDate())
                .build();
    }

    public static ItemRequest mapToItemRequest(ItemRequestDto dto) {
        Set<Long> itemIds = new HashSet<>();
        if (dto.getItems() != null && !dto.getItems().isEmpty()) {
            itemIds = dto.getItems().stream()
                    .map(ItemResponse::getItemId)
                    .collect(Collectors.toSet());
        }

        return ItemRequest.builder()
                .description(dto.getDescription())
                .items(itemIds)
                .build();
    }
}
