package ru.practicum.shareit.request.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponse;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class RequestMapper {

    public ItemRequestDto mapToDto(ItemRequest request, List<ItemResponse> itemResponses) {

        return ItemRequestDto.builder()
                .id(request.getId())
                .description(request.getDescription())
                .items(itemResponses)
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
