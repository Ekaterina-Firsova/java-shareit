package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

/**
 * Entity item
 * contains information about items
 */
@Data
@Builder
public class Item {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private Boolean available;
    @NotBlank
    private User owner;
    private String request;
}
