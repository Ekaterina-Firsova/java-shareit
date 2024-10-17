package ru.practicum.shareit.user;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Update;


/**
 * Controller for users
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Validated(Create.class) @RequestBody UserDto userDto) {
        log.info("Request: create user: {}", userDto);
        return userClient.create(userDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Request: GET /users");
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUser(@PathVariable long userId) {
        log.info("Request: GET /users/{}", userId);
        return userClient.getById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(
            @NotNull(message = "Message is null") @PathVariable@NotNull Long userId,
            @Validated(Update.class) @RequestBody UserDto userDto) {
        log.info("Request: PATCH /users/{} with body: {}",userId, userDto);
        return userClient.patch(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        log.info("Request DELETE /users/{}", userId);
        userClient.delete(userId);
    }
}