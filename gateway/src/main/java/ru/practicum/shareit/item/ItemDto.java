package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.comment.CommentDto;
import ru.practicum.shareit.request.ItemRequestDto;
import ru.practicum.shareit.user.UserDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object representing an Item.
 */
@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private UserDto owner;
    private ItemRequestDto request;
    private Long requestId;
    private List<CommentDto> comments;
    private LocalDateTime lastBooking;     // Последнее завершенное бронирование
    private LocalDateTime nextBooking;     // Ближайшее будущее бронирование
}
