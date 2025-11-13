package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.SameEmailException;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceIml;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {
    @Mock
    private UserRepository repository;
    private UserService service;

    private User user;
    private User user2;
    private UserDTO userDTO;

    @BeforeEach
    void before() {
        service = new UserServiceIml(repository);
        user = User.builder()
                .id(1L)
                .name("Shrek")
                .email("shrekIsLove@gmail.com")
                .build();

        user2 = User.builder()
                .id(2L)
                .name("Fiona")
                .email("fiona@gmail.com")
                .build();

        userDTO = UserDTO.builder()
                .name("Donkey")
                .email("donkey@swamp.com")
                .build();
    }

    @Test
    void testGetAllUsers() {
        Mockito.when(repository.findAll())
                .thenReturn(Arrays.asList(user, user2));

        List<UserDTO> users = service.allUsers();

        assertThat(users, hasSize(2));
        assertThat(users.get(0).getName(), is("Shrek"));
        assertThat(users.get(1).getName(), is("Fiona"));
        Mockito.verify(repository).findAll();
    }

    @Test
    void testGetAllUsersEmpty() {
        Mockito.when(repository.findAll())
                .thenReturn(List.of());

        List<UserDTO> users = service.allUsers();

        assertThat(users, empty());
        Mockito.verify(repository).findAll();
    }

    @Test
    void testGetUserById() {
        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(user));

        UserDTO result = service.userById(1L);

        assertThat(result.getId(), is(1L));
        assertThat(result.getName(), is("Shrek"));
        assertThat(result.getEmail(), is("shrekIsLove@gmail.com"));
        Mockito.verify(repository).findById(1L);
    }

    @Test
    void testGetUserByIdNotFound() {
        Mockito.when(repository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.userById(999L));
        Mockito.verify(repository).findById(999L);
    }

    @Test
    void testCreateUser() {
        Mockito.when(repository.existsByEmail("donkey@swamp.com"))
                .thenReturn(false);
        Mockito.when(repository.save(Mockito.any(User.class)))
                .thenAnswer(invocation -> {
                    User userToSave = invocation.getArgument(0);
                    userToSave.setId(3L);
                    return userToSave;
                });

        UserDTO result = service.createUser(userDTO);

        assertThat(result.getId(), is(3L));
        assertThat(result.getName(), is("Donkey"));
        assertThat(result.getEmail(), is("donkey@swamp.com"));
        Mockito.verify(repository).existsByEmail("donkey@swamp.com");
        Mockito.verify(repository).save(Mockito.any(User.class));
    }

    @Test
    void testCreateUserWithDuplicateEmail() {
        Mockito.when(repository.existsByEmail("donkey@swamp.com"))
                .thenReturn(true);

        assertThrows(SameEmailException.class, () -> service.createUser(userDTO));
        Mockito.verify(repository).existsByEmail("donkey@swamp.com");
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void testUpdateUser() {
        UserDTO updateDto = UserDTO.builder()
                .name("Updated Shrek")
                .email("updated@email.com")
                .build();

        Mockito.when(repository.existsByEmail("updated@email.com"))
                .thenReturn(false);
        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(repository.save(Mockito.any(User.class)))
                .thenReturn(user);

        UserDTO result = service.updateUser(1L, updateDto);

        assertThat(result.getName(), is("Updated Shrek"));
        assertThat(result.getEmail(), is("updated@email.com"));
        Mockito.verify(repository).existsByEmail("updated@email.com");
        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).save(Mockito.any(User.class));
    }

    @Test
    void testUpdateUserPartialName() {
        UserDTO updateDto = UserDTO.builder()
                .name("Updated Shrek")
                .build();

        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(repository.save(Mockito.any(User.class)))
                .thenReturn(user);

        UserDTO result = service.updateUser(1L, updateDto);

        assertThat(result.getName(), is("Updated Shrek"));
        assertThat(result.getEmail(), is("shrekIsLove@gmail.com"));
        Mockito.verify(repository, Mockito.never()).existsByEmail(Mockito.anyString());
        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).save(Mockito.any(User.class));
    }

    @Test
    void testUpdateUserPartialEmail() {
        UserDTO updateDto = UserDTO.builder()
                .email("updated@email.com")
                .build();

        Mockito.when(repository.existsByEmail("updated@email.com"))
                .thenReturn(false);
        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(repository.save(Mockito.any(User.class)))
                .thenReturn(user);

        UserDTO result = service.updateUser(1L, updateDto);

        assertThat(result.getEmail(), is("updated@email.com"));
        assertThat(result.getName(), is("Shrek"));
        Mockito.verify(repository).existsByEmail("updated@email.com");
        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).save(Mockito.any(User.class));
    }

    @Test
    void testUpdateUserWithDuplicateEmail() {
        UserDTO updateDto = UserDTO.builder()
                .email("fiona@gmail.com")
                .build();

        Mockito.when(repository.existsByEmail("fiona@gmail.com"))
                .thenReturn(true);

        assertThrows(SameEmailException.class, () -> service.updateUser(1L, updateDto));
        Mockito.verify(repository).existsByEmail("fiona@gmail.com");
        Mockito.verify(repository, Mockito.never()).findById(Mockito.anyLong());
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void testUpdateUserNotFound() {
        UserDTO updateDto = UserDTO.builder()
                .name("Updated")
                .build();

        Mockito.when(repository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> service.updateUser(999L, updateDto));
        Mockito.verify(repository).findById(999L);
        Mockito.verify(repository, Mockito.never()).save(Mockito.any(User.class));
    }

    @Test
    void testDeleteUser() {
        service.deleteUser(333L);

        Mockito.verify(repository, Mockito.times(1))
                .deleteById(333L);
    }

    @Test
    void testCreateUserWithNullFields() {
        UserDTO userWithNullFields = UserDTO.builder()
                .name(null)
                .email(null)
                .build();

        Mockito.when(repository.existsByEmail(null))
                .thenReturn(false);
        Mockito.when(repository.save(Mockito.any(User.class)))
                .thenAnswer(invocation -> {
                    User userToSave = invocation.getArgument(0);
                    userToSave.setId(4L);
                    return userToSave;
                });

        UserDTO result = service.createUser(userWithNullFields);

        assertThat(result.getId(), is(4L));
        assertThat(result.getName(), nullValue());
        assertThat(result.getEmail(), nullValue());
        Mockito.verify(repository).existsByEmail(null);
        Mockito.verify(repository).save(Mockito.any(User.class));
    }

    @Test
    void testUpdateUserWithEmptyDTO() {
        UserDTO emptyDto = UserDTO.builder().build();

        Mockito.when(repository.findById(1L))
                .thenReturn(Optional.of(user));
        Mockito.when(repository.save(Mockito.any(User.class)))
                .thenReturn(user);

        UserDTO result = service.updateUser(1L, emptyDto);

        assertThat(result.getName(), is("Shrek"));
        assertThat(result.getEmail(), is("shrekIsLove@gmail.com"));
        Mockito.verify(repository, Mockito.never()).existsByEmail(Mockito.anyString());
        Mockito.verify(repository).findById(1L);
        Mockito.verify(repository).save(Mockito.any(User.class));
    }
}
