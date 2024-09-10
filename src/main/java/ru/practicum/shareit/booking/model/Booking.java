package ru.practicum.shareit.booking.model;

import ru.practicum.shareit.user.model.User;

import java.util.Date;

/**
 * TODO Sprint add-bookings.
 */
public class Booking {
    private Long id;
    private Date start;
    private Date end;
    private Long itemId;
    private User booker;
    private Status status;
}
