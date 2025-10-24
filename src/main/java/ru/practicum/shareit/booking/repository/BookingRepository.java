package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusBook;


import java.time.Instant;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByItemOwnerId(long ownerId);

    List<Booking> findByItemOwnerIdAndStartDateBeforeAndEndDateAfter(long ownerId, Instant now1, Instant now2);

    List<Booking> findByItemOwnerIdAndEndDateBefore(long ownerId, Instant now);

    List<Booking> findByItemOwnerIdAndStartDateAfter(long ownerId, Instant now);

    List<Booking> findByItemOwnerIdAndStatusContaining(long ownerId, String status);

    List<Booking> findByBookerId(long bookerId);

    List<Booking> findByBookerIdAndStartDateBeforeAndEndDateAfter(long ownerId, Instant now1, Instant now2);

    List<Booking> findByBookerIdAndEndDateBefore(long ownerId, Instant now);

    List<Booking> findByBookerIdAndStartDateAfter(long ownerId, Instant now);

    List<Booking> findByBookerIdAndStatusContaining(long ownerId, String status);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN FETCH b.item i " +
            "WHERE i.id = ?1 AND b.startDate >= CURRENT_TIMESTAMP " +
            "ORDER BY b.startDate ASC " +
            "LIMIT 1")
    Booking getNearliestFutureBooking(Long itemId);

    @Query("SELECT b " +
            "FROM Booking b " +
            "JOIN FETCH b.item i " +
            "WHERE i.id = ?1 AND b.endDate < CURRENT_TIMESTAMP " +
            "ORDER BY b.endDate DESC " +
            "LIMIT 1")
    Booking getNearliestPastBooking(Long itemId);

    boolean existsByItemId(long itemId);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.booker.id = :userId " +
            "AND b.item.id = :itemId AND b.endDate <= CURRENT_TIMESTAMP " +
            "AND b.status = :status")
    boolean existsByBookerIdAndItemIdAndEndBefore(@Param("userId") Long userId,
                                                  @Param("itemId") Long itemId,
                                                  @Param("status") StatusBook status);
}
