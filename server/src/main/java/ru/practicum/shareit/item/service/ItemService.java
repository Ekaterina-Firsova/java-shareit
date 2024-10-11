package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.service.CrudService;

import java.util.List;

public interface ItemService extends CrudService<ItemDto> {

    ItemDto update(Long userId, Long itemId, ItemDto updatedItem);

    ItemDto getById(Long id);

    ItemDto getById(Long id, Long userId);

    List<ItemDto> getAllItemsByUser(Long userId);

    List<ItemDto> getText(String text);

    CommentDto createComment(Long itemId, CommentDto comment, Long userId);

}
