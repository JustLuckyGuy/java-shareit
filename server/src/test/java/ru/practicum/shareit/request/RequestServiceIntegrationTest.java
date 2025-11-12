package ru.practicum.shareit.request;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(classes = ShareItServer.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class RequestServiceIntegrationTest {
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final ItemRequestService service;

    private User user;
    private User user2;
    private ItemRequest request;
    private ItemRequest request2;
    private ItemRequest request3;

    @BeforeEach
    void before() {
        user = User.builder()
                .name("Shrek")
                .email("shrekIsLove@gmail.com")
                .build();

        user2 = User.builder()
                .name("Donkey")
                .email("shrekIsLife@gmail.com")
                .build();

        request = ItemRequest.builder()
                .user(user)
                .description("I wanna some shrexy stuff")
                .build();

        request2 = ItemRequest.builder()
                .user(user)
                .description("Swampy swampy swampy swamp")
                .build();

        request3 = ItemRequest.builder()
                .user(user2)
                .description("CUPCAKES")
                .build();
    }

    @Test
    void testCreateRequest() {
        userRepository.save(user);

        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Need a new swamp")
                .build();

        ItemRequestDto created = service.createRequest(requestDto, user.getId());

        assertThat(created.getId(), notNullValue());
        assertThat(created.getDescription(), is("Need a new swamp"));
        assertThat(created.getCreated(), notNullValue());
    }

    @Test
    void testCreateRequestUserNotFound() {
        ItemRequestDto requestDto = ItemRequestDto.builder()
                .description("Need a new swamp")
                .build();

        assertThrows(NotFoundException.class, () -> service.createRequest(requestDto, 999L));
    }

    @Test
    void testGetUserRequests() {
        userRepository.save(user);
        userRepository.save(user2);
        requestRepository.save(request);
        requestRepository.save(request2);
        requestRepository.save(request3);

        List<ItemRequestDto> resp = service.getUserRequests(user.getId());

        assertThat(resp.size(), is(2));
        assertThat(resp.getLast().getDescription(), is(request2.getDescription()));
    }

    @Test
    void testGetUserRequestsEmpty() {
        userRepository.save(user);

        List<ItemRequestDto> resp = service.getUserRequests(user.getId());

        assertThat(resp, empty());
    }

    @Test
    void testGetAllRequests() {
        userRepository.save(user);
        userRepository.save(user2);
        requestRepository.save(request);
        requestRepository.save(request2);
        requestRepository.save(request3);

        List<ItemRequestDto> resp = service.getAllRequests(user.getId());

        assertThat(resp.size(), is(1));
        assertThat(resp.getFirst().getDescription(), is(request3.getDescription()));
    }

    @Test
    void testGetAllRequestsEmpty() {
        userRepository.save(user);
        userRepository.save(user2);
        requestRepository.save(request);
        requestRepository.save(request2);

        List<ItemRequestDto> resp = service.getAllRequests(user.getId());

        assertThat(resp, empty());
    }

    @Test
    void testGetAllRequestsNoOtherUsers() {
        userRepository.save(user);
        requestRepository.save(request);

        List<ItemRequestDto> resp = service.getAllRequests(user.getId());

        assertThat(resp, empty());
    }

    @Test
    void testGetRequestById() {
        userRepository.save(user);
        requestRepository.save(request);

        ItemRequestDto resp = service.getRequestById(request.getId());

        assertThat(resp.getDescription(), is(request.getDescription()));
    }

    @Test
    void testGetRequestByIdNotFound() {
        assertThrows(NotFoundException.class, () -> service.getRequestById(999L));
    }

    @Test
    void testRequestOrderByCreatedDate() {
        userRepository.save(user);

        ItemRequest oldRequest = ItemRequest.builder()
                .user(user)
                .description("Old request")
                .createdDate(LocalDateTime.now().minusDays(2))
                .build();

        ItemRequest newRequest = ItemRequest.builder()
                .user(user)
                .description("New request")
                .createdDate(LocalDateTime.now().minusDays(1))
                .build();

        requestRepository.save(oldRequest);
        requestRepository.save(newRequest);

        List<ItemRequestDto> resp = service.getUserRequests(user.getId());

        assertThat(resp.size(), is(2));
        assertThat(resp.get(0).getDescription(), is("New request"));
        assertThat(resp.get(1).getDescription(), is("Old request"));
    }

    @Test
    void testRequestWithItems() {
        userRepository.save(user);
        requestRepository.save(request);

        ItemRequestDto resp = service.getRequestById(request.getId());

        assertThat(resp.getDescription(), is(request.getDescription()));
        assertThat(resp.getItems(), notNullValue());
    }
}
