package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query("""
        SELECT b 
            FROM Booking b 
            WHERE b.booker.id = :userId AND b.start <= CURRENT_TIMESTAMP AND b.end >= CURRENT_TIMESTAMP
            """)
    List<Booking> findCurrentBookings(Long userId);
    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.start > CURRENT_TIMESTAMP ORDER BY b.start DESC")
    List<Booking> findFutureBookings(Long userId);

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.end < CURRENT_TIMESTAMP")
    List<Booking> findPastBookings(Long userId);


    @Query("SELECT b FROM Booking b WHERE b.booker.id = :userId AND b.status = :status")
    List<Booking> findBookingsByStatus(Long userId, BookingStatus status);

    List<Booking> findAllByBookerId(Long userId);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStart(
            Long ownerId, LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerIdAndStartAfterOrderByStart(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStart(Long ownerId, LocalDateTime now);

    List<Booking> findByItemOwnerIdAndStatusOrderByStart(Long ownerId, BookingStatus status);

    List<Booking> findByItemOwnerIdOrderByStart(Long ownerId);
}
