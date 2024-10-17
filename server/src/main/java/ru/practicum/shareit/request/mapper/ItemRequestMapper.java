package ru.practicum.shareit.request.mapper;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemOwnerDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

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
                .requester(itemRequestDto.getRequester())
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
//        else {
//            // Создаем список с пустой структурой, если items нет
//            itemOwnerDto = List.of(createEmptyItemOwnerDto());
//        }

        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .requester(itemRequest.getRequester())
                .created(itemRequest.getCreated())
                .items(itemOwnerDto)
                .build();
    }

    private ItemOwnerDto mapToItemOwnerDto(Item item) {
        return ItemOwnerDto.builder()
                .id(item.getId())
                .name(item.getName())
                .owner(item.getOwner())
                .build();
    }

//    private ItemOwnerDto createEmptyItemOwnerDto() {
//        return ItemOwnerDto.builder()
//                .id(0L) // Присваиваем дефолтные значения
//                .name("") // Стандартное имя для пустого объекта
//                .owner(null) // Нет владельца
//                .build();
//    }
}
