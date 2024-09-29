package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Item item = Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .request(itemDto.getRequest())
                .build();

        return ItemMapper.mapToItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto updatedItem) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item not found"));

        if (Objects.equals(item.getOwner().getId(), userId)) {
            if (updatedItem.getAvailable() != null) {
                item.setAvailable(updatedItem.getAvailable());
            }
            if (updatedItem.getRequest() != null) {
                item.setRequest(updatedItem.getRequest());
            }
            if (updatedItem.getName() != null) {
                item.setName(updatedItem.getName());
            }
            if (updatedItem.getDescription() != null) {
                item.setDescription(updatedItem.getDescription());
            }
            return ItemMapper.mapToItemDto(itemRepository.save(item));
        }
        throw new NotFoundException("The user with ID = " + userId + " is not the owner");
    }


    @Override
    public List<ItemDto> getAll() {
        return itemRepository.findAll()
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public ItemDto getById(Long id) {
        return ItemMapper.mapToItemDto(itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Item not found")));
    }

    @Override
    public List<ItemDto> getAllFromUser(Long userId) {
        return itemRepository.findByOwnerId(userId)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> getText(String text) {
        if (text == null || text.isEmpty()) {
            return List.of();
        }
        return itemRepository.findByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text)
                .stream()
                .map(ItemMapper::mapToItemDto)
                .toList();

    }
}
