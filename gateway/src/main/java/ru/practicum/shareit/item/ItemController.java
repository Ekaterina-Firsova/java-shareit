package ru.practicum.shareit.item;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Update;

import java.util.List;


/**
 * Controller for users
 */
@Slf4j
@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Validated(Create.class) @RequestBody ItemDto itemDto) {
        log.info("Request POST /items with X-Sharer-User-Id: {} and body : {}", userId, itemDto);
        return itemClient.post(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
            @NotNull(message = "Item id cannot be null") @PathVariable Long id,
            @Validated(Update.class) @RequestBody ItemDto itemDto) {
        log.info("Request PATCH /items/{} with X-Sharer-User-Id: {}", id, userId);
        return itemClient.patch(userId, itemDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Request GET /items from user {}", userId);
        return itemClient.getAll(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getUser(@PathVariable long itemId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Request GET /id: {} X-Sharer-User-Id: {}", itemId, userId);
        return itemClient.getById(itemId, userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getItem(@RequestParam String text) {
        log.info("Request GET /search: {}", text);
        return itemClient.getText(text);
    }

    @PostMapping("{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @PathVariable @NotNull Long itemId,
                             @RequestBody final CommentDto comment) {
        log.info("Request POST /items/{}/comment with X-Sharer-User-Id: {}", itemId, userId);
        return itemClient.createComment(itemId, comment, userId);
    }
}