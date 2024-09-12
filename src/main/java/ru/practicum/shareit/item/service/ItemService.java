package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long userId, Long id, ItemDto updatedItem);

    List<ItemDto> getAll();

    ItemDto getById(Long id);

    List<ItemDto> getAllFromUser(Long userId);

    List<ItemDto> getText(String text);
}
