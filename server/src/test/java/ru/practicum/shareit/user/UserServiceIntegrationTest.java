package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@SpringBootTest(classes = ShareItServer.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceIntegrationTest {
    final UserService service;
    final UserRepository repository;

    User user;

    @BeforeEach
    void before() {
        user = User.builder()
                .name("Shrek")
                .email("shrekIsLove@gmail.com")
                .build();
    }

    @Test
    void testGetUser() {
        repository.save(user);

        UserDTO resp = service.userById(user.getId());

        assertThat(resp.getEmail(), is(user.getEmail()));
        assertThat(resp.getName(), is(user.getName()));
    }

    @Test
    void testUpdateUser() {
        repository.save(user);

        UserDTO dto = UserDTO.builder().name("TREBUSHET").build();

        UserDTO resp = service.updateUser(user.getId(), dto);

        assertThat(resp.getName(), is(dto.getName()));
        assertThat(resp.getEmail(), is(user.getEmail()));
    }
}
