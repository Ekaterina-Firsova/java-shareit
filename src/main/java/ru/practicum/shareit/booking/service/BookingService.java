package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.service.CrudService;

import java.util.List;

public interface BookingService extends CrudService<BookingDto> {

    BookingDto update(Long ownerId, Long bookingId, Boolean approved);

    BookingDto getById(Long id, Long userId);

    List<BookingDto> getBookingsByState(Long userId, String state);
}
