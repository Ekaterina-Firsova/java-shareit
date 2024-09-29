package ru.practicum.shareit.booking.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserServiceImpl userService;
    private final ItemServiceImpl itemService;

    @Override
    public BookingDto create(Long userId, BookingDto bookingDto) {
        User booker = UserMapper.mapToUser(userService.getById(userId));

        bookingDto.setStatus(BookingStatus.WAITING);
        Item item = ItemMapper.mapToItem(itemService.getById(bookingDto.getItemId()));
        if (!item.getAvailable()) {
            throw new InvalidDataException("Item with ID = " + bookingDto.getItemId() + " is not available");
        }

        Booking booking = BookingMapper.mapToBooking(bookingDto, booker, item);

        Booking savedBooking = bookingRepository.save(booking);

        return BookingMapper.mapToBookingDto(savedBooking);
    }

    @Override
    public BookingDto update(Long ownerId, Long bookingId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (booking.getId() == null) {
            throw new InvalidDataException("Booking with ID = " + bookingId + " not found");
        }
        if (!Objects.equals(booking.getItem().getOwner().getId(), ownerId)) {
            throw new InvalidDataException("The user with ID = " + ownerId
                    + " is not the owner of item with ID = " + booking.getItem().getId());
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.mapToBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException("User is neither the booker nor the owner");
        }

        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByState(Long userId, String state) {
        List<Booking> bookings = switch (state.toUpperCase()) {
            case "CURRENT" -> bookingRepository.findCurrentBookings(userId);
            case "PAST" -> bookingRepository.findPastBookings(userId);
            case "FUTURE" -> bookingRepository.findFutureBookings(userId);
            case "WAITING" -> bookingRepository.findBookingsByStatus(userId, BookingStatus.WAITING);
            case "REJECTED" -> bookingRepository.findBookingsByStatus(userId, BookingStatus.REJECTED);
            default -> bookingRepository.findAllByBookerId(userId);
        };
        return bookings
                .stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    public List<BookingDto> getBookingOwner(String state, Long userId) {
        if (state == null || state.isEmpty()) {
            throw new IllegalArgumentException("State must not be null or empty");
        }
        LocalDateTime now = LocalDateTime.now();

        List<Booking> bookings = switch (state.toUpperCase()) {
            case "CURRENT" -> bookingRepository.findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStart(
                    userId, now, now);
            case "PAST" -> bookingRepository.findByItemOwnerIdAndEndBeforeOrderByStart(userId, now);
            case "FUTURE" -> bookingRepository.findByItemOwnerIdAndStartAfterOrderByStart(userId, now);
            case "WAITING" -> bookingRepository.findByItemOwnerIdAndStatusOrderByStart(userId, BookingStatus.WAITING);
            case "REJECTED" -> bookingRepository.findByItemOwnerIdAndStatusOrderByStart(userId, BookingStatus.REJECTED);
            default -> bookingRepository.findByItemOwnerIdOrderByStart(userId);
        };
        return bookings
                .stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }

    @Override
    public List<BookingDto> getAll() {
        return List.of();
    }

    @Override
    public BookingDto getById(Long id) {
        return null;
    }

}

