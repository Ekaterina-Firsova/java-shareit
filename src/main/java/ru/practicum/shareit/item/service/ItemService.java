package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.service.CrudService;

import java.util.List;

public interface ItemService extends CrudService<ItemDto> {

//    ItemDto create(Long userId, ItemDto itemDto);
//
    ItemDto update(Long userId, Long itemId, ItemDto updatedItem);
//
//    List<ItemDto> getAll();
//
//    ItemDto getById(Long id);

    List<ItemDto> getAllFromUser(Long userId);

    List<ItemDto> getText(String text);

    CommentDto createComment(Long itemId, CommentDto comment, Long userId);
}
