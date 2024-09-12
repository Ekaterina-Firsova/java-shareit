package ru.practicum.shareit.item.mapper;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@UtilityClass
@NotNull
public class ItemMapper {
    public Item mapToItem(final ItemDto itemDto) {

        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(itemDto.getOwner())
                .request(itemDto.getRequest())
                .available(itemDto.getAvailable())
                .build();
    }

    public ItemDto mapToItemDto(final Item item) {

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(item.getOwner())
                .request(item.getRequest())
                .available(item.getAvailable())
                .build();
    }
}
