package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponse;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final RequestRepository repository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final RequestMapper mapper;

    @Override
    public ItemRequestDto createRequest(ItemRequestDto dto, long userId) {
        ItemRequest request = RequestMapper.mapToItemRequest(dto);
        request.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден")));
        List<ItemResponse> items = collectItemsToRequest(request);

        return mapper.mapToDto(repository.save(request), items);
    }

    @Override
    public List<ItemRequestDto> getUserRequests(long userId) {
        return repository.findByUserIdOrderByCreatedDateDesc(userId).stream()
                .map(itemRequest -> {
                    List<ItemResponse> itemResponses = collectItemsToRequest(itemRequest);
                    return mapper.mapToDto(itemRequest, itemResponses);
                })
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId) {
        return repository.findByUserIdNotOrderByCreatedDateDesc(userId).stream()
                .map(itemRequest -> {
                    List<ItemResponse> itemResponses = collectItemsToRequest(itemRequest);
                    return mapper.mapToDto(itemRequest, itemResponses);
                })
                .toList();
    }

    @Override
    public ItemRequestDto getRequestById(long requestId) {
        ItemRequest request = repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Предмет с id: " + requestId + " не найден"));
        List<ItemResponse> items = collectItemsToRequest(request);
        return mapper.mapToDto(request, items);
    }

    private List<ItemResponse> collectItemsToRequest(ItemRequest itemRequest) {
        List<ItemResponse> items = new ArrayList<>();
        if (itemRequest.getItems() != null && !itemRequest.getItems().isEmpty()) {
            items = itemRequest.getItems().stream()
                    .map(id -> {
                        Item item = itemRepository.findById(id)
                                .orElseThrow(() -> new NotFoundException("Предмет с id: " + id + " не найден"));
                        return ItemResponse.builder()
                                .itemId(item.getId())
                                .userId(item.getOwner().getId())
                                .name(item.getName())
                                .description(item.getDescription())
                                .build();
                    })
                    .toList();
        }
        return items;
    }
}

