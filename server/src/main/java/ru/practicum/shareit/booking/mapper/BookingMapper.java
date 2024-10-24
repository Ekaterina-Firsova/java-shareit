package ru.practicum.shareit.booking.mapper;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class BookingMapper {
    public BookingDto mapToBookingDto(@NotNull Booking booking) {

        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .booker(UserMapper.mapToUserDto(booking.getBooker()))
                .status(booking.getStatus())
                .item(ItemMapper.mapToItemDto(booking.getItem(), null, null, null))
                .build();
    }

    public static Booking mapToBooking(@NotNull BookingDto bookingDto, User booker, Item item) {

        return Booking.builder()
                .id(bookingDto.getId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(bookingDto.getStatus())
                .build();
    }
}
