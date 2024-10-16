package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto create(Long userId, BookingDto bookingDto);

    BookingDto update(Long ownerId, Long bookingId, Boolean approved);

    BookingDto getById(Long id, Long userId);

    List<BookingDto> getBookingsByState(Long userId, String state);

    List<BookingDto> getBookingOwner(String state, Long userId);
}
