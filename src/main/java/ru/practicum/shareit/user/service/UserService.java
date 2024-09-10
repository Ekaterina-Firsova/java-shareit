package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.service.CrudService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserService implements CrudService<UserDto> {
    private final UserStorage userStorage;

    @Override
    public UserDto create(UserDto userDto) {
        return UserMapper.mapToUserDto(userStorage.create(UserMapper.mapToUser(userDto)));
    }

    @Override
    public Collection<UserDto> getAll() {
        return userStorage.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto getById(Long id) {
        return UserMapper.mapToUserDto(userStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("User with ID = " + id + " not found.")));
    }

    @Override
    public void delete(long id) {
        userStorage.delete(id);
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        return UserMapper.mapToUserDto(userStorage.update(id, UserMapper.mapToUser(userDto)));
    }


}
