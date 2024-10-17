package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Update;

@Data
@Builder
public class UserDto {
    private Long id;

    @NotBlank(message = "Name is mandatory", groups = Create.class)
    private String name;

    @Email(message = "Email should be in correct format", groups = {Create.class, Update.class})
    @NotBlank(message = "Email is required", groups = Create.class)
    private String email;
}
