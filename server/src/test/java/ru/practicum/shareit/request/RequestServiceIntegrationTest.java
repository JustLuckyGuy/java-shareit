package ru.practicum.shareit.request;

import lombok.AccessLevel;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SpringBootTest(classes = ShareItServer.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
class RequestServiceIntegrationTest {
    final UserRepository userRepository;
    final RequestRepository requestRepository;
    final ItemRequestService service;

    User user;
    User user2;
    ItemRequest request;
    ItemRequest request2;
    ItemRequest request3;

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
    void testGetRequestById() {
        userRepository.save(user);
        requestRepository.save(request);

        ItemRequestDto resp = service.getRequestById(request.getId());

        assertThat(resp.getDescription(), is(request.getDescription()));
    }
}
