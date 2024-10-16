package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        if (userDto.getName() == null || userDto.getName().isBlank()) {
            throw new InvalidDataException("Name cannot be empty");
        }
        if (userDto.getEmail() == null || userDto.getEmail().isBlank()) {
            throw new InvalidDataException("Email cannot be empty");
        }
        if (!isValidEmail(userDto.getEmail())) {
            throw new InvalidDataException("Email should be correct format");
        }
        return UserMapper.mapToUserDto(userRepository.save(UserMapper.mapToUser(userDto)));
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }

    @Transactional
    @Override
    public UserDto update(Long id, UserDto userDto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id));
        if (userDto.getName() != null && !userDto.getName().isEmpty()) {
            existingUser.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isEmpty()) {
            existingUser.setEmail(userDto.getEmail());
        }
        return UserMapper.mapToUserDto(userRepository.save(existingUser));
    }

    @Override
    public Collection<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto getById(Long id) {
        return UserMapper.mapToUserDto(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id: " + id)));
    }

    @Transactional
    @Override
    public void delete(long id) {
        userRepository.deleteById(id);
    }

}
