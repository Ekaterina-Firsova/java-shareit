package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.shareit.validator.Create;

/**
 * Controller for users
 */
@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                                         @Validated(Create.class) @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Request POST /requests with X-Sharer-User-Id: {} and body : {}", userId, itemRequestDto);
        return itemRequestClient.post(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> GetUserRequests(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Request GET /requests with X-Sharer-User-Id: {}", userId);
        return itemRequestClient.getAllByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Request GET /requests/all with X-Sharer-User-Id: {}", userId);
        return itemRequestClient.getAll(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getUser(@PathVariable long requestId,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Request GET /requests/{} with X-Sharer-User-Id: {}",requestId, userId);
        return itemRequestClient.getById(requestId, userId);
    }

}