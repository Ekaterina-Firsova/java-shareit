package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Data Transfer Object representing an Item.
 */
@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(message = "Name cannot be empty")
    private String name;
    @NotBlank(message = "Description cannot be empty")
    private String description;
    @NotNull(message = "Availability must be specified")
    private Boolean available;
    @NotNull(message = "Owner must be specified")
    private UserDto owner;
    private ItemRequestDto request;
    private Long requestId;
    private List<CommentDto> comments;
    @PastOrPresent(message = "Last booking date must be in the past or present")
    private LocalDateTime lastBooking;     // Последнее завершенное бронирование
    @Future(message = "Next booking date must be in the future")
    private LocalDateTime nextBooking;     // Ближайшее будущее бронирование
}
