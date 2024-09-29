package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody final ItemDto itemDto) {
        log.info("Request POST /items with X-Sharer-User-Id: {} and body : {}", userId, itemDto);
        return itemService.create(userId, itemDto);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable @NotNull Long id,
                              @RequestBody @Valid ItemDto updatedItem) {
        log.info("Request PATCH /id: {}", id);
        return itemService.update(userId, id, updatedItem);
    }

    @GetMapping
    public Collection<ItemDto> getAllFromUser(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("Request GET /items from user {}", userId);
        return itemService.getAllFromUser(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable long id) {
        log.info("Request GET /id: {}", id);
        return itemService.getById(id);
    }

    @GetMapping("/search")
    public List<ItemDto> getItem(@RequestParam String text) {
        log.info("Request GET /search: {}", text);
        return itemService.getText(text);
    }
}

