package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    String COMMON_QUERY = "SELECT b FROM Booking b "
            + "INNER JOIN Item i ON b.item.id = i.id "
            + "WHERE i.ownerId = :ownerId ";

    @Modifying
    @Query("UPDATE Booking b "
            + "SET b.status = :status  "
            + "WHERE b.id = :bookingId")
    void save(BookingStatus status, Long bookingId);

    List<Booking> findAllByBookerIdOrderByEndDesc(Long bookerId);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Long bookerId,
                                                                               LocalDateTime start, LocalDateTime end);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByEndDesc(long bookerId, LocalDateTime end);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByEndDesc(long bookerId, LocalDateTime start);

    List<Booking> findAllByBookerIdAndStatusOrderByEndDesc(long bookerId, BookingStatus status);


    List<Booking> findByItemOwnerIdOrderByEndDesc(long ownerId);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Long bookerId,
                                                                               LocalDateTime start, LocalDateTime end);
    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByEndDesc(long bookerId, LocalDateTime end);


    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByEndDesc(long ownerId, LocalDateTime now);


    List<Booking> findAllByItemOwnerIdAndStatusOrderByEndDesc(long ownerId, BookingStatus bookingStatus);

    List<Booking> findAllByItemIdOrderByEndDesc(long itemId);
}
