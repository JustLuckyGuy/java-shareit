package ru.practicum.shareit.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceIml;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceUnitTest {
    @Mock
    UserRepository repository;
    UserService service;

    @BeforeEach
    void before() {
        service = new UserServiceIml(repository);
    }

    @Test
    void testDeleteUser() {
        service.deleteUser(333L);

        Mockito.verify(repository, Mockito.times(1))
                .deleteById(333L);
    }
}
