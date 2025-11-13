package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.SameEmailException;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest(classes = ShareItServer.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceIntegrationTest {
    private final UserService service;
    private final UserRepository repository;

    private User user;
    private User user2;

    @BeforeEach
    void before() {
        user = User.builder()
                .name("Shrek")
                .email("shrekIsLove@gmail.com")
                .build();

        user2 = User.builder()
                .name("Fiona")
                .email("fiona@gmail.com")
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
    void testGetUserNotFound() {
        assertThrows(NotFoundException.class, () -> service.userById(999L));
    }

    @Test
    void testCreateUser() {
        UserDTO userDto = UserDTO.builder()
                .name("Donkey")
                .email("donkey@swamp.com")
                .build();

        UserDTO created = service.createUser(userDto);

        assertThat(created.getId(), notNullValue());
        assertThat(created.getName(), is("Donkey"));
        assertThat(created.getEmail(), is("donkey@swamp.com"));
    }

    @Test
    void testCreateUserWithDuplicateEmail() {
        repository.save(user);

        UserDTO userDto = UserDTO.builder()
                .name("Another Shrek")
                .email("shrekIsLove@gmail.com")
                .build();

        assertThrows(SameEmailException.class, () -> service.createUser(userDto));
    }


    @Test
    void testUpdateUser() {
        repository.save(user);

        UserDTO dto = UserDTO.builder().name("TREBUSHET").build();

        UserDTO resp = service.updateUser(user.getId(), dto);

        assertThat(resp.getName(), is(dto.getName()));
        assertThat(resp.getEmail(), is(user.getEmail()));
    }

    @Test
    void testUpdateUserFullData() {
        repository.save(user);

        UserDTO dto = UserDTO.builder()
                .name("Updated Name")
                .email("updated@email.com")
                .build();

        UserDTO resp = service.updateUser(user.getId(), dto);

        assertThat(resp.getName(), is("Updated Name"));
        assertThat(resp.getEmail(), is("updated@email.com"));
    }

    @Test
    void testUpdateUserWithDuplicateEmail() {
        repository.save(user);
        repository.save(user2);

        UserDTO dto = UserDTO.builder()
                .email("fiona@gmail.com")
                .build();

        assertThrows(SameEmailException.class, () -> service.updateUser(user.getId(), dto));
    }

    @Test
    void testUpdateUserNotFound() {
        UserDTO dto = UserDTO.builder().name("Updated").build();

        assertThrows(NotFoundException.class, () -> service.updateUser(999L, dto));
    }

    @Test
    void testDeleteUser() {
        repository.save(user);

        service.deleteUser(user.getId());

        assertThrows(NotFoundException.class, () -> service.userById(user.getId()));
    }

    @Test
    void testDeleteUserNotFound() {
        service.deleteUser(999L);
    }

    @Test
    void testGetAllUsers() {
        repository.save(user);
        repository.save(user2);

        List<UserDTO> users = service.allUsers();

        assertThat(users, hasSize(2));
        assertThat(users.stream().map(UserDTO::getName).toList(),
                containsInAnyOrder("Shrek", "Fiona"));
    }

    @Test
    void testGetAllUsersEmpty() {
        List<UserDTO> users = service.allUsers();

        assertThat(users, empty());
    }

    @Test
    void testUpdateUserPartialFields() {
        repository.save(user);

        UserDTO emailUpdateDto = UserDTO.builder()
                .email("newshrek@swamp.com")
                .build();

        UserDTO afterEmailUpdate = service.updateUser(user.getId(), emailUpdateDto);
        assertThat(afterEmailUpdate.getEmail(), is("newshrek@swamp.com"));
        assertThat(afterEmailUpdate.getName(), is("Shrek"));

        UserDTO nameUpdateDto = UserDTO.builder()
                .name("Shrek Updated")
                .build();

        UserDTO afterNameUpdate = service.updateUser(user.getId(), nameUpdateDto);
        assertThat(afterNameUpdate.getName(), is("Shrek Updated"));
        assertThat(afterNameUpdate.getEmail(), is("newshrek@swamp.com"));
    }


    @Test
    void testUserPersistenceAndRetrieval() {
        UserDTO originalDto = UserDTO.builder()
                .name("Test User")
                .email("test@test.com")
                .build();

        UserDTO created = service.createUser(originalDto);
        UserDTO retrieved = service.userById(created.getId());

        assertThat(retrieved.getId(), is(created.getId()));
        assertThat(retrieved.getName(), is("Test User"));
        assertThat(retrieved.getEmail(), is("test@test.com"));
    }
}
