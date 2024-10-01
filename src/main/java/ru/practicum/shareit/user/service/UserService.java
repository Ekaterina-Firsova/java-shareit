package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

public interface UserService {

    UserDto create(UserDto t);

    UserDto update(Long id, UserDto t);

    Collection<UserDto> getAll();

    UserDto getById(Long id);

    void delete(long id);
}
