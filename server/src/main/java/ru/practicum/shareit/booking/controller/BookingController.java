package ru.practicum.shareit.booking.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingServiceImpl;
    private final UserService userServiceImpl;

    @PostMapping
    public BookingDto create(@RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestBody final BookingDto bookingDto) {
        log.info("Request POST /bookings with X-Sharer-User-Id: {} and body : {}", userId, bookingDto);
        return bookingServiceImpl.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                              @PathVariable @NotNull Long bookingId,
                              @RequestParam Boolean approved
                              ) {
        log.info("Request PATCH /bookingId: {} ?approved= {}", bookingId, approved);
        return bookingServiceImpl.update(ownerId, bookingId, approved);
    }


    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long bookingId) {
        log.info("Request GET /bookingId: {}", bookingId);
        return bookingServiceImpl.getById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByState(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Request GET /bookings?state={} by userId={}", state, userId);
        userServiceImpl.getById(userId);
        return bookingServiceImpl.getBookingsByState(userId, state);
    }

    @GetMapping("owner")
    public List<BookingDto> getBookingOwner(@RequestHeader("X-Sharer-User-Id") Long userId,
                                      @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Request GET /bookingId/owner?state={} for X-Sharer-User-Id {}", state, userId);
        userServiceImpl.getById(userId);
        return bookingServiceImpl.getBookingOwner(state, userId);
    }


}
