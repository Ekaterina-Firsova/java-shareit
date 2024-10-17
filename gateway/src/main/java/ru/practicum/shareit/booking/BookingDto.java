package ru.practicum.shareit.booking;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.ItemDto;
import ru.practicum.shareit.user.UserDto;
import ru.practicum.shareit.validator.Create;
import ru.practicum.shareit.validator.Update;


import java.time.LocalDateTime;

/**
 * Data Transfer Object representing a Booking.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {
    private Long id;

    @FutureOrPresent(message = "Date should be present or future", groups = {Create.class, Update.class})
    private LocalDateTime start;

    @Future(message = "Date should be future", groups = {Create.class, Update.class})
    private LocalDateTime end;

    private Long itemId;

    private ItemDto item;

    private UserDto booker;

    private BookingStatus status;
}
