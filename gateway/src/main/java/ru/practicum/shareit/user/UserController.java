package ru.practicum.shareit.user;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDTO;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserController {
    private final UserClient client;

    @PostMapping
    public ResponseEntity<UserDTO> saveUser(@RequestBody @Validated UserDTO userDto) {
        log.info("Post user {}", userDto);
        return client.saveUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUser(@PathVariable @NotNull @Positive long userId) {
        log.info("Get user with id {}", userId);
        return client.getUser(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable long userId, @RequestBody UserDTO userDto) {
        log.info("Update user {}, userId={}", userDto, userId);
        return client.updateUser(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<UserDTO> deleteUser(@PathVariable @NotNull @Positive long userId) {
        log.info("Delete user with id {}", userId);
        return client.deleteUser(userId);
    }
}
