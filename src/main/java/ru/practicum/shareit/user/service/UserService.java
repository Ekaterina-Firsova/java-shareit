package ru.practicum.shareit.user.service;

import java.util.Collection;

public interface UserService<T> {

    T create(T t);

    T update(Long id, T t);

    Collection<T> getAll();

    T getById(Long id);

    void delete(long id);
}
