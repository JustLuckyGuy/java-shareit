package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDTO> allUsers() {
        return userService.allUsers();
    }

    @GetMapping("/{userId}")
    public UserDTO getUser(@PathVariable long userId) {
        return userService.userById(userId);
    }

    @PostMapping
    public UserDTO saveUser(@Validated @RequestBody UserDTO userDto) {
        return userService.createUser(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDTO updateUser(@PathVariable long userId, @RequestBody UserDTO userDto) {
        return userService.updateUser(userId, userDto);
    }

    @DeleteMapping("/{id}")
    public void removeUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

}
