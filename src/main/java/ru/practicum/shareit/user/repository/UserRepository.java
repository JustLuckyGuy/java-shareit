package ru.practicum.shareit.user.repository;


import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface UserRepository {
    Collection<User> findAllUsers();
    User findUserById(Long userId);
    User save(User user);
    User update(Long userId, User user);
    void delete(Long userId);
    void isExistsByEmail(String email);
}
