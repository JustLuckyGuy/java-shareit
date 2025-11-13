package ru.practicum.shareit.user.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDTO;

import java.util.List;

@Transactional(readOnly = true)
public interface UserService {
    List<UserDTO> allUsers();

    UserDTO userById(Long userId);

    @Transactional
    UserDTO createUser(UserDTO user);

    @Transactional
    UserDTO updateUser(Long userId, UserDTO user);

    @Transactional
    void deleteUser(Long userId);
}
