package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserServiceIml implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDTO> allUsers() {
        log.info("Пользователи: {}", userRepository.findAllUsers());
        return userRepository.findAllUsers().stream().map(UserMapper::mapToDTO).toList();
    }

    @Override
    public UserDTO userById(Long userId) {
        return UserMapper.mapToDTO(userRepository.findUserById(userId));
    }

    @Override
    public UserDTO createUser(UserDTO user) {
        userRepository.isExistsByEmail(user.getEmail());
        User newUser = UserMapper.mapToUser(user);
        return UserMapper.mapToDTO(userRepository.save(newUser));
    }

    @Override
    public UserDTO updateUser(Long userId, UserDTO user) {
        userRepository.isExistsByEmail(user.getEmail());
        User newUser = UserMapper.mapToUser(user);
        return UserMapper.mapToDTO(userRepository.update(userId, newUser));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.delete(userId);
    }

}
