package ru.practicum.shareit.booking;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ru.practicum.shareit.validator.Create;


@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @GetMapping
    public ResponseEntity<Object> getBookingsByState(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Request GET /bookings?state={} by userId={}", state, userId);
        return bookingClient.getBookingsByState(userId, state);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestBody @Validated(Create.class) BookingDto bookingDto) {
        log.info("Request POST /bookings with X-Sharer-User-Id: {} and body : {}", userId, bookingDto);
        return bookingClient.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable @NotNull @Positive Long bookingId,
                                                @RequestParam Boolean approved) {
        log.info("Request PATCH /bookingId: {} ?approved= {}", bookingId, approved);
        return bookingClient.update(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable @NotNull @Positive Long bookingId) {
        log.info("Request GET /bookingId: {} with X-Sharer-User-Id: {}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingOwner(@RequestHeader("X-Sharer-User-Id") long userId,
                                                  @RequestParam(required = false, defaultValue = "ALL") String state) {
        log.info("Request GET /bookingId/owner?state={} for X-Sharer-User-Id {}", state, userId);
        return bookingClient.getBookingOwner(userId, state);
    }
}