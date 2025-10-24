package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.SameEmailException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceIml implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDTO> allUsers() {
        log.info("Пользователи: {}", userRepository.findAll());
        return userRepository.findAll().stream().map(UserMapper::mapToDTO).toList();
    }

    @Override
    public UserDTO userById(Long userId) {
        return UserMapper.mapToDTO(userRepository.findById(userId)
                .orElseThrow(()->new NotFoundException("Пользователь не найден")));
    }

    @Override
    public UserDTO createUser(UserDTO user) {
        if(userRepository.existsByEmail(user.getEmail())){
            throw new SameEmailException("Пользователь с этим email уже существует");
        }

        User newUser = UserMapper.mapToUser(user);
        return UserMapper.mapToDTO(userRepository.save(newUser));
    }

    @Override
    public UserDTO updateUser(Long userId, UserDTO user) {
        if(userRepository.existsByEmail(user.getEmail())){
            throw new SameEmailException("Данный email уже занят");
        }
        User oldUser = userRepository.findById(userId)
                .orElseThrow(()->new NotFoundException("Пользователь не найден"));
        oldUser = UserMapper.updateFields(oldUser, UserMapper.mapToUser(user));
        return UserMapper.mapToDTO(userRepository.save(oldUser));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

}
