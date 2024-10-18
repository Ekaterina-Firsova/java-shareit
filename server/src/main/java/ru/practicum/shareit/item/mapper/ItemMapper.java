package ru.practicum.shareit.item.mapper;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

@UtilityClass
@NotNull
public class ItemMapper {
    public Item mapToItem(@NotNull final ItemDto itemDto) {

        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .owner(UserMapper.mapToUser(itemDto.getOwner()))
                .request(ItemRequestMapper.mapToItemRequest(itemDto.getRequest()))
                .available(itemDto.getAvailable())
                .build();
    }

    public ItemDto mapToItemDto(@NotNull final Item item,
                                List<CommentDto> comments,
                                LocalDateTime lastBookingDate,
                                LocalDateTime nextBookingDate) {

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .owner(UserMapper.mapToUserDto(item.getOwner()))
                .request(ItemRequestMapper.mapToItemRequestDto(item.getRequest()))
                .available(item.getAvailable())
                .comments(comments)
                .lastBooking(lastBookingDate)
                .nextBooking(nextBookingDate)
                .build();
    }
}
