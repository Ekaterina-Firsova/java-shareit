package ru.practicum.shareit.user.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.common.IdGenerator;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component
public class UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    public User create(User user) {
        if (user.getEmail() == null) {
            throw new IllegalArgumentException("Отсутствует Email");
        }
        checkUser(user);
        user.setId(IdGenerator.getNextId(users));
        users.put(user.getId(), user);
        return user;
    }

    private void checkUser(User user) {
        // Проверяем, существует ли уже пользователь с таким email
        for (User existingUser : users.values()) {
            if (existingUser.getEmail().equals(user.getEmail())) {
                throw new IllegalArgumentException("Пользователь с такой почтой уже существует");
            }
        }
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public Optional<User> getById(Long userId) {
        return Optional.ofNullable(users.get(userId));
    }

    public User update(Long id, User newUser) {
        if (users.containsKey(id)) {
            User oldUser = users.get(id);
            if (newUser.getName() != null) {
                oldUser.setName(newUser.getName());
            }
            if (newUser.getEmail() != null) {
                checkUser(newUser);
                oldUser.setEmail(newUser.getEmail());
            }
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id = " + newUser.getId() + " не найден");
    }

    public void delete(long id) {
        users.remove(id);
    }
}
