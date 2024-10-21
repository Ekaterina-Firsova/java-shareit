package ru.practicum.shareit.request.mapper;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
@NotNull
public class ItemRequestMapper {
    public ItemRequest mapToItemRequest(final ItemRequestDto itemRequestDto) {
        if (itemRequestDto == null) {
            return null;
        }

        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requester(UserMapper.mapToUser(itemRequestDto.getRequester()))
                .created(itemRequestDto.getCreated())
                .build();
    }

    public ItemRequestDto mapToItemRequestDto(final ItemRequest itemRequest) {
        if (itemRequest == null) {
            return null;
        }

        List<ItemOwnerDto> itemOwnerDto = List.of();
        if (itemRequest.getItems() != null && !itemRequest.getItems().isEmpty()) {
            itemOwnerDto = itemRequest.getItems().stream()
                    .map(ItemRequestMapper::mapToItemOwnerDto)
                    .collect(Collectors.toList());
        }

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(UserMapper.mapToUserDto(itemRequest.getRequester()))
                .created(itemRequest.getCreated())
                .items(itemOwnerDto)
                .build();
    }

    private ItemOwnerDto mapToItemOwnerDto(Item item) {
        return ItemOwnerDto.builder()
                .id(item.getId())
                .name(item.getName())
                .owner(UserMapper.mapToUserDto(item.getOwner()))
                .build();
    }
}
