package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final RequestRepository repository;
    private final UserRepository userRepository;
    private final RequestMapper mapper;

    @Override
    public ItemRequestDto createRequest(ItemRequestDto dto, long userId) {
        ItemRequest request = RequestMapper.mapToItemRequest(dto);
        request.setUser(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id: " + userId + " не найден")));

        return mapper.mapToDto(repository.save(request));
    }

    @Override
    public List<ItemRequestDto> getUserRequests(long userId) {
        return repository.findByUserIdOrderByCreatedDateDesc(userId).stream()
                .map(mapper::mapToDto)
                .toList();
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long userId) {
        return repository.findByUserIdNotOrderByCreatedDateDesc(userId).stream()
                .map(mapper::mapToDto)
                .toList();
    }

    @Override
    public ItemRequestDto getRequestById(long requestId) {
        return mapper.mapToDto(repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Предмет с id: " + requestId + " не найден")));
    }
}

