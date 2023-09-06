package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Modifying
    @Query("UPDATE Booking b "
            + "SET b.status = :status  "
            + "WHERE b.id = :bookingId")
    void save(BookingStatus status, Long bookingId);

    List<Booking> findAllByBookerIdOrderByEndDesc(Long bookerId, Pageable page);

    List<Booking> findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Long bookerId,
                                                                               LocalDateTime start,
                                                                               LocalDateTime end,
                                                                               Pageable page);

    List<Booking> findAllByBookerIdAndEndIsBeforeOrderByEndDesc(long bookerId, LocalDateTime end, Pageable page);

    List<Booking> findAllByBookerIdAndStartIsAfterOrderByEndDesc(long bookerId, LocalDateTime start, Pageable page);

    List<Booking> findAllByBookerIdAndStatusOrderByEndDesc(long bookerId, BookingStatus status, Pageable page);


    List<Booking> findByItemOwnerIdOrderByEndDesc(long ownerId, Pageable page);

    List<Booking> findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(Long bookerId,
                                                                                  LocalDateTime start,
                                                                                  LocalDateTime end,
                                                                                  Pageable page);

    List<Booking> findAllByItemOwnerIdAndEndIsBeforeOrderByEndDesc(long bookerId, LocalDateTime end, Pageable page);


    List<Booking> findAllByItemOwnerIdAndStartIsAfterOrderByEndDesc(long ownerId, LocalDateTime now, Pageable page);


    List<Booking> findAllByItemOwnerIdAndStatusOrderByEndDesc(long ownerId, BookingStatus bookingStatus, Pageable page);

    List<Booking> findAllByItemIdOrderByEndDesc(long itemId);

    List<Booking> findAllByItemIdAndBookerIdAndStatusIsAndStartIsBeforeOrderByStart(long itemId,
                                                                                    long bookerId,
                                                                                    BookingStatus status,
                                                                                    LocalDateTime end);
}
