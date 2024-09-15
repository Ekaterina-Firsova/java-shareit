package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

/**
 * Entity user
 * contains information about users
 */
@Data
@Builder
public class User {
    private Long id;
    @NotBlank
    private String name;
    @Email(message = "Email should be correct format")
    @NotBlank
    private String email;
}
