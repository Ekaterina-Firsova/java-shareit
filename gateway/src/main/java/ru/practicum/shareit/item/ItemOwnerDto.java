package ru.practicum.shareit.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.UserDto;

/**
 * Data Transfer Object representing an Item.
 */
@Data
@Builder
public class ItemOwnerDto {
    private Long id;
    private String name;
    private UserDto owner;
}
