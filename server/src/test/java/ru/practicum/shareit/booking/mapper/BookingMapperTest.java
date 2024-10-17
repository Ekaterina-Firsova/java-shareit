package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import static org.junit.jupiter.api.Assertions.assertNull;

public class BookingMapperTest {

    @Test
    public void testMapToBookingDto_BookingIsNull_ShouldReturnNull() {
        Booking booking = null;

        BookingDto result = BookingMapper.mapToBookingDto(booking);

        assertNull(result, "Expected result to be null when booking is null");
    }

    @Test
    public void testMapToBooking_BookingDtoIsNull_ShouldReturnNull() {
        BookingDto bookingDto = null;

        Booking result = BookingMapper.mapToBooking(bookingDto, null, null);

        assertNull(result, "Expected result to be null when booking is null");
    }

}