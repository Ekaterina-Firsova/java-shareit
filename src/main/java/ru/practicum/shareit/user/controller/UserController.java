package ru.practicum.shareit.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody final UserDto userDto) {
        log.info("Request POST /users with body : {}", userDto);
        return userService.create(userDto);
    }

    @GetMapping
    public Collection<UserDto> getAll() {
        log.info("Request GET /users");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        log.info("Request GET /id: {}", id);
        return userService.getById(id);
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable @NotNull Long id, @RequestBody @Valid UserDto updatedUser) {
        log.info("Request PATCH /id: {}", id);
        return userService.update(id, updatedUser);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable long id) {
        log.info("Request DELETE /id: {}", id);
        userService.delete(id);
    }
}
