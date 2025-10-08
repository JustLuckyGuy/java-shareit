package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDTO;

public final class UserMapper {
    public static UserDTO mapToDTO(User user){
        return UserDTO.builder()
                .email(user.getEmail())
                .name(user.getName())
                .build();

    }
}
