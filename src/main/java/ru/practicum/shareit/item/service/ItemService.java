package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {

  ItemDto create(Long userId, ItemDto itemDto);

  ItemDto update(Long userId, Long id, ItemDto updatedItem);

  Collection<ItemDto> getAll();

  ItemDto getById(Long id);

  Collection<ItemDto> getAllFromUser(Long userId);

  Collection<ItemDto> getText(String text);
}
