package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.SameEmailException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Optional;

@Component
public class UserRepositoryIml implements UserRepository {
    private static long id = 1;
    private final HashMap<Long, User> users = new HashMap<>();

    @Override
    public Collection<User> findAllUsers() {
        return users.values();
    }

    @Override
    public User findUserById(Long userId) {
        isUserExist(userId);
        return users.get(userId);
    }

    @Override
    public User save(User user) {
        user.setId(id);
        users.put(id, user);
        id++;
        return user;
    }

    @Override
    public User update(Long userId, User user) {
        isUserExist(userId);
        User existingUser = users.get(userId);
        existingUser.setName(user.getName() == null ? existingUser.getName() : user.getName());
        existingUser.setEmail(user.getEmail() == null ? existingUser.getEmail() : user.getEmail());
        users.put(userId, existingUser);
        return users.get(userId);
    }

    @Override
    public void delete(Long userId) {
        isUserExist(userId);
        users.remove(userId);
    }

    private void isUserExist(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("Пользователь с id: " + userId + " не найден");
        }
    }

    @Override
    public void isExistsByEmail(String email) {
        if (email != null) {
            Optional<User> sameEmail = users.values().stream()
                    .filter(user -> user.getEmail().equals(email))
                    .findAny();

            if (sameEmail.isPresent()) {
                throw new SameEmailException("Такая почта уже существует");
            }
        }
    }
}
