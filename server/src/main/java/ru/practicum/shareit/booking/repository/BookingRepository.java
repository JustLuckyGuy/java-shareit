package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByItemOwnerId(long ownerId);

    List<Booking> findByItemOwnerIdAndStartDateBeforeAndEndDateAfter(long ownerId, LocalDateTime now1, LocalDateTime now2, Sort sort);

    List<Booking> findByItemOwnerIdAndEndDateBefore(long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerIdAndStartDateAfter(long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByItemOwnerIdAndStatusContaining(long ownerId, String status, Sort sort);

    List<Booking> findByBookerId(long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStartDateBeforeAndEndDateAfter(long ownerId, LocalDateTime now1, LocalDateTime now2, Sort sort);

    List<Booking> findByBookerIdAndEndDateBefore(long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStartDateAfter(long ownerId, LocalDateTime now, Sort sort);

    List<Booking> findByBookerIdAndStatusContaining(long ownerId, String status, Sort sort);

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

   List<Booking> findByItemIdAndBookerId(Long userId, Long itemId);

    @Query("select b from Booking b where b.item in ?1 and b.status = 'APPROVED'")
    List<Booking> findApprovedForItems(Collection<Item> items, Sort sort);

}
