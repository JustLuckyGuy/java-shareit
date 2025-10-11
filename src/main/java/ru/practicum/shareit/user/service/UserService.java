package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDTO;

import java.util.List;

public interface UserService {
    List<UserDTO> allUsers();
    UserDTO userById(Long userId);
    UserDTO createUser(UserDTO user);
    UserDTO updateUser(Long userId, UserDTO user);
    void deleteUser(Long userId);
}
