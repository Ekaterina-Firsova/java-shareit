package ru.practicum.shareit.booking.service;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.InvalidDataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.instancio.Select.field;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateBooking() {
        long userId = 1L;
        long itemId = 2L;
        LocalDateTime now = LocalDateTime.now();

        ItemDto itemDto = Instancio.of(ItemDto.class)
                .set(field(ItemDto::getId), itemId)
                .set(field(ItemDto::getAvailable), true)
                .create();

        BookingDto bookingDto = Instancio.of(BookingDto.class)
                .set(field(BookingDto::getStart), now.plusDays(1))
                .set(field(BookingDto::getEnd), now.plusDays(2))
                .set(field(BookingDto::getItemId), itemId)
                .set(field(BookingDto::getStatus), BookingStatus.WAITING)
                .create();

        User user = Instancio.of(User.class).create();
        Item item = Instancio.of(Item.class).create();
        Booking booking = BookingMapper.mapToBooking(bookingDto, user, item);

        Mockito.when(userService.getById(userId)).thenReturn(UserMapper.mapToUserDto(user));
        Mockito.when(itemService.getById(itemId)).thenReturn(itemDto);
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.create(userId, bookingDto);

        assertEquals(BookingStatus.WAITING, result.getStatus());
        Mockito.verify(bookingRepository).save(Mockito.any(Booking.class));
    }

    @Test
    public void testCreateBooking_InvalidTime() {
        long userId = 1L;
        BookingDto bookingDto = Instancio.of(BookingDto.class)
                .set(field(BookingDto::getStart), LocalDateTime.now().plusDays(1))
                .set(field(BookingDto::getEnd), LocalDateTime.now().minusDays(1))
                .create();

        assertThrows(InvalidDataException.class, () -> {
            bookingService.create(userId, bookingDto);
        });

        Mockito.verify(bookingRepository, Mockito.never()).save(Mockito.any(Booking.class));
    }

    @Test
    void testUpdateBookingApproved() {
        Long ownerId = 1L;
        Long bookingId = 1L;
        Booking booking = Instancio.of(Booking.class)
                .set(field(Booking::getId), bookingId)
                .create();

        Item item = new Item();
        item.setId(2L);

        User owner = new User();
        owner.setId(ownerId);
        item.setOwner(owner);
        booking.setItem(item);

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.update(ownerId, bookingId, true);

        assertNotNull(result);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
        Mockito.verify(bookingRepository).save(booking);
    }

    @Test
    void testUpdateBookingRejected() {
        Long ownerId = 1L;
        Long bookingId = 1L;
        Booking booking = Instancio.of(Booking.class)
                .set(field(Booking::getId), bookingId)
                .set(field(Booking::getStatus), BookingStatus.REJECTED)
                .create();

        Item item = new Item();
        item.setId(2L);
        User owner = new User();
        owner.setId(ownerId);
        item.setOwner(owner);
        booking.setItem(item);

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(Mockito.any(Booking.class))).thenReturn(booking);

        BookingDto result = bookingService.update(ownerId, bookingId, false);

        assertNotNull(result);
        assertEquals(BookingStatus.REJECTED, booking.getStatus());
        Mockito.verify(bookingRepository).save(booking);
    }

    @Test
    void testUpdateBookingNotFound() {
        Long ownerId = 1L;
        Long bookingId = 1L;

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.update(ownerId, bookingId, true));
        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void testUpdateInvalidOwner() {
        Long ownerId = 1L;
        Long bookingId = 1L;
        Long anotherOwnerId = 2L;

        Booking booking = new Booking();
        booking.setId(bookingId);
        Item item = new Item();
        item.setId(2L);
        User owner = new User();
        owner.setId(anotherOwnerId);
        item.setOwner(owner);
        booking.setItem(item);

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        InvalidDataException exception = assertThrows(InvalidDataException.class,
                () -> bookingService.update(ownerId, bookingId, true));
        assertEquals("The user with ID = " + ownerId + " is not the owner of item with ID = " + item.getId(), exception.getMessage());
    }

    @Test
    void testGetByIdSuccessForBooker() {
        Long bookingId = 1L;
        Long userId = 2L;
        Booking booking = Instancio.of(Booking.class)
                .set(field(Booking::getId), bookingId)
                .create();

        User booker = new User();
        booker.setId(userId);
        booking.setBooker(booker);

        Item item = new Item();
        User owner = new User();
        owner.setId(3L); // другой владелец
        item.setOwner(owner);
        booking.setItem(item);

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getById(bookingId, userId);

        assertNotNull(result);
        Mockito.verify(bookingRepository).findById(bookingId);
    }

    @Test
    void testGetByIdSuccessForOwner() {
        Long bookingId = 1L;
        Long userId = 2L;
        Booking booking = new Booking();
        booking.setId(bookingId);

        User booker = new User();
        booker.setId(3L); // другой пользователь (не владелец)
        booking.setBooker(booker);

        Item item = new Item();
        User owner = new User();
        owner.setId(userId); // текущий пользователь - владелец
        item.setOwner(owner);
        booking.setItem(item);

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto result = bookingService.getById(bookingId, userId);

        assertNotNull(result);
        Mockito.verify(bookingRepository).findById(bookingId);
    }

    @Test
    void testGetByIdBookingNotFound() {
        Long bookingId = 1L;
        Long userId = 2L;

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getById(bookingId, userId));
        assertEquals("Booking not found", exception.getMessage());
    }

    @Test
    void testGetByIdUserIsNeitherBookerNorOwner() {
        Long bookingId = 1L;
        Long userId = 2L; // Текущий пользователь

        Booking booking = new Booking();
        booking.setId(bookingId);

        User booker = new User();
        booker.setId(3L); // другой пользователь (не текущий)
        booking.setBooker(booker);

        Item item = new Item();
        User owner = new User();
        owner.setId(4L); // другой пользователь (не текущий)
        item.setOwner(owner);
        booking.setItem(item);

        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> bookingService.getById(bookingId, userId));
        assertEquals("User is neither the booker nor the owner", exception.getMessage());
    }

    @Test
    void testGetBookingsByStateCurrent() {
        Long userId = 1L;
        List<Booking> bookings = List.of(
                Instancio.of(Booking.class)
                        .create(),
                Instancio.of(Booking.class)
                        .create()
        );

        Mockito.when(bookingRepository.findCurrentBookings(userId)).thenReturn(bookings);
        List<BookingDto> result = bookingService.getBookingsByState(userId, "CURRENT");

        assertNotNull(result);
        assertEquals(2, result.size());
        Mockito.verify(bookingRepository).findCurrentBookings(userId);
    }

    @Test
    void testGetBookingsByStatePast() {
        Long userId = 1L;
        List<Booking> bookings = List.of(
                Instancio.of(Booking.class)
                        .create(),
                Instancio.of(Booking.class)
                        .create()
        );

        Mockito.when(bookingRepository.findPastBookings(userId)).thenReturn(bookings);
        List<BookingDto> result = bookingService.getBookingsByState(userId, "PAST");

        assertNotNull(result);
        assertEquals(2, result.size());
        Mockito.verify(bookingRepository).findPastBookings(userId);
    }

    @Test
    void testGetBookingsByStateFuture() {
        Long userId = 1L;
        List<Booking> bookings = List.of(
                Instancio.of(Booking.class)
                        .create(),
                Instancio.of(Booking.class)
                        .create()
        );

        Mockito.when(bookingRepository.findFutureBookings(userId)).thenReturn(bookings);

        List<BookingDto> result = bookingService.getBookingsByState(userId, "FUTURE");

        assertNotNull(result);
        assertEquals(2, result.size());
        Mockito.verify(bookingRepository).findFutureBookings(userId);
    }

    @Test
    void testGetBookingsByStateWaiting() {
        Long userId = 1L;
        List<Booking> bookings = List.of(
                Instancio.of(Booking.class)
                        .create(),
                Instancio.of(Booking.class)
                        .create()
        );

        Mockito.when(bookingRepository.findBookingsByStatus(userId, BookingStatus.WAITING)).thenReturn(bookings);

        List<BookingDto> result = bookingService.getBookingsByState(userId, "WAITING");

        assertNotNull(result);
        assertEquals(2, result.size());
        Mockito.verify(bookingRepository).findBookingsByStatus(userId, BookingStatus.WAITING);
    }

    @Test
    void testGetBookingsByStateRejected() {
        Long userId = 1L;
        List<Booking> bookings = List.of(
                Instancio.of(Booking.class)
                        .create(),
                Instancio.of(Booking.class)
                        .create()
        );

        Mockito.when(bookingRepository.findBookingsByStatus(userId, BookingStatus.REJECTED)).thenReturn(bookings);

        List<BookingDto> result = bookingService.getBookingsByState(userId, "REJECTED");

        assertNotNull(result);
        assertEquals(2, result.size());
        Mockito.verify(bookingRepository).findBookingsByStatus(userId, BookingStatus.REJECTED);
    }

    @Test
    void testGetBookingsByStateDefault() {
        Long userId = 1L;
        List<Booking> bookings = List.of(
                Instancio.of(Booking.class)
                        .create(),
                Instancio.of(Booking.class)
                        .create()
        );

        Mockito.when(bookingRepository.findAllByBookerId(userId)).thenReturn(bookings);

        List<BookingDto> result = bookingService.getBookingsByState(userId, "UNKNOWN");

        assertNotNull(result);
        assertEquals(2, result.size());
        Mockito.verify(bookingRepository).findAllByBookerId(userId);
    }

    @Test
    public void testGetBookingOwner_NullState_ThrowsException() {
        Long userId = 1L;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.getBookingOwner(null, userId);
        });
        assertEquals("State must not be null or empty", exception.getMessage());
    }

    @Test
    public void testGetBookingOwner_EmptyState_ThrowsException() {
        Long userId = 1L;
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            bookingService.getBookingOwner("", userId);
        });
        assertEquals("State must not be null or empty", exception.getMessage());
    }

    @Test
    public void testGetBookingOwner_CurrentState() {
        Long userId = 1L;
        List<Booking> bookings = List.of(
                Instancio.of(Booking.class)
                        .create(),
                Instancio.of(Booking.class)
                        .create()
        );
        Mockito.when(bookingRepository
                        .findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStart(
                                Mockito.anyLong(),
                                Mockito.any(LocalDateTime.class),
                                Mockito.any(LocalDateTime.class))
                )
                .thenReturn(bookings);

        List<BookingDto> result = bookingService.getBookingOwner("CURRENT", userId);

        assertEquals(2, result.size());
        Mockito.verify(bookingRepository).findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStart(
                Mockito.anyLong(),
                Mockito.any(LocalDateTime.class),
                Mockito.any(LocalDateTime.class)
        );
    }

    @Test
    void testGetBookingsOwnerByStatePast() {
        Long userId = 1L;
        List<Booking> bookings = List.of(
                Instancio.of(Booking.class)
                        .create(),
                Instancio.of(Booking.class)
                        .create()
        );
        Mockito.when(bookingRepository
                        .findByItemOwnerIdAndEndBeforeOrderByStart(
                                Mockito.anyLong(),
                                Mockito.any(LocalDateTime.class))
                )
                .thenReturn(bookings);

        List<BookingDto> result = bookingService.getBookingOwner("PAST", userId);

        assertEquals(2, result.size());
        Mockito.verify(bookingRepository).findByItemOwnerIdAndEndBeforeOrderByStart(
                Mockito.anyLong(),
                Mockito.any(LocalDateTime.class)
        );
    }

    @Test
    void testGetBookingsOwnerByStateFuture() {
        Long userId = 1L;
        List<Booking> bookings = List.of(
                Instancio.of(Booking.class)
                        .create(),
                Instancio.of(Booking.class)
                        .create()
        );
        Mockito.when(bookingRepository
                        .findByItemOwnerIdAndStartAfterOrderByStart(
                                Mockito.anyLong(),
                                Mockito.any(LocalDateTime.class))
                )
                .thenReturn(bookings);

        List<BookingDto> result = bookingService.getBookingOwner("FUTURE", userId);

        assertEquals(2, result.size());
        Mockito.verify(bookingRepository).findByItemOwnerIdAndStartAfterOrderByStart(
                Mockito.anyLong(),
                Mockito.any(LocalDateTime.class)
        );
    }

    @Test
    void testGetBookingsOwnerByStateWaiting() {
        Long userId = 1L;
        List<Booking> bookings = List.of(
                Instancio.of(Booking.class)
                        .create(),
                Instancio.of(Booking.class)
                        .create()
        );
        Mockito.when(bookingRepository
                        .findByItemOwnerIdAndStatusOrderByStart(
                                userId,
                                BookingStatus.WAITING)
                )
                .thenReturn(bookings);

        List<BookingDto> result = bookingService.getBookingOwner("WAITING", userId);

        assertEquals(2, result.size());
        Mockito.verify(bookingRepository).findByItemOwnerIdAndStatusOrderByStart(
                userId,
                BookingStatus.WAITING
        );
    }

    @Test
    void testGetBookingsOwnerByStateRejected() {
        Long userId = 1L;
        List<Booking> bookings = List.of(
                Instancio.of(Booking.class)
                        .create(),
                Instancio.of(Booking.class)
                        .create()
        );
        Mockito.when(bookingRepository
                        .findByItemOwnerIdAndStatusOrderByStart(
                                userId,
                                BookingStatus.REJECTED)
                )
                .thenReturn(bookings);

        List<BookingDto> result = bookingService.getBookingOwner("REJECTED", userId);

        assertEquals(2, result.size());
        Mockito.verify(bookingRepository).findByItemOwnerIdAndStatusOrderByStart(
                userId,
                BookingStatus.REJECTED
        );
    }

    @Test
    void testGetBookingsOwnerByStateDefault() {
        Long userId = 1L;
        List<Booking> bookings = List.of(
                Instancio.of(Booking.class)
                        .create(),
                Instancio.of(Booking.class)
                        .create()
        );

        Mockito.when(bookingRepository.findByItemOwnerIdOrderByStart(userId)).thenReturn(bookings);

        List<BookingDto> result = bookingService.getBookingOwner("UNKNOWN", userId);

        assertNotNull(result);
        assertEquals(2, result.size());
        Mockito.verify(bookingRepository).findByItemOwnerIdOrderByStart(userId);
    }
}