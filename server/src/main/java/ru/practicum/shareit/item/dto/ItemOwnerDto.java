package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

/**
 * Data Transfer Object representing an Item.
 */
@Data
@Builder
public class ItemOwnerDto {
    private Long id;
    private String name;
    private User owner;
}
