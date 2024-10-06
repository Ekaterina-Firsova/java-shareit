package ru.practicum.shareit.request.service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.service.CrudService;

import java.util.List;

public interface ItemRequestService {

    @Transactional
    ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getAllRequestsByUser(Long userId);

    List<ItemRequestDto> getAll(Long userId);

    ItemRequestDto getById(Long id);


}
