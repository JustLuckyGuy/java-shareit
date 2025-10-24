package ru.practicum.shareit.user.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDTO;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class UserMapper {
    public static UserDTO mapToDTO(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .build();

    }

    public static User mapToUser(UserDTO user) {
        return User.builder()
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static User updateFields(User oldUser, User newUser){
            oldUser.setEmail(newUser.getEmail() == null ? oldUser.getEmail() : newUser.getEmail());
            oldUser.setName(newUser.getName() == null ? oldUser.getName() : newUser.getName());
        return oldUser;
    }
}
