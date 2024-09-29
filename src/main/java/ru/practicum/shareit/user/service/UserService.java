package ru.practicum.shareit.user.service;

import java.util.Collection;

public interface UserService<User> {

    User create(User t);

    User update(Long id, User t);

    Collection<User> getAll();

    User getById(Long id);

    void delete(long id);
}
