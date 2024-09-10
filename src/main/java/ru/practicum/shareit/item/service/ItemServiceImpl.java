package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        if (itemDto.getAvailable() == null) {
            throw new InvalidDataException("Не указана возможность аренды");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new InvalidDataException("Не указано название");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new InvalidDataException("Не указано описание");
        }
        User user = userStorage.getById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));

        return ItemMapper.mapToItemDto(itemStorage.create(ItemMapper.mapToItem(itemDto), user));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto itemDto) {
        if (checkOwner(userId, itemId)) {
            return ItemMapper.mapToItemDto(itemStorage.update(itemId, ItemMapper.mapToItem(itemDto)));
        }
        throw new NotFoundException("Пользователь с ID = " + userId + " не является владельцем");
    }

    private boolean checkOwner(Long userId, Long itemId) {
        Item item = itemStorage.getById(itemId)
                .orElseThrow(() -> new NotFoundException("Пользователь с ID = " + userId + " не найден"));
        return Objects.equals(item.getOwner().getId(), userId);
    }


    @Override
    public Collection<ItemDto> getAll() {
        return itemStorage.findAll().stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto getById(Long id) {
        return ItemMapper.mapToItemDto(itemStorage.getById(id)
                .orElseThrow(() -> new NotFoundException("Item with ID = " + id + " not found.")));
    }

    @Override
    public Collection<ItemDto> getAllFromUser(Long userId) {
        return itemStorage.findAll().stream()
                .map(ItemMapper::mapToItemDto)
                .filter(itemDto -> Objects.equals(itemDto.getOwner().getId(), userId))
                .toList();
    }

    @Override
    public Collection<ItemDto> getText(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return itemStorage.findAll().stream()
                .map(ItemMapper::mapToItemDto)
                .filter(itemDto -> itemDto.getAvailable() &
                        itemDto.getName().toLowerCase().contains(text.toLowerCase())
                        || itemDto.getDescription().toLowerCase().contains(text.toLowerCase())
                )
                .toList();
    }
}
