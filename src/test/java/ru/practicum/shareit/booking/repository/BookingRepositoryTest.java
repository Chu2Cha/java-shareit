package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private User booker1;

    private Booking booking1;

    private Booking booking2;

    private Item item1;

    Pageable page = PageRequest.of(0 / 10, 10);


    @BeforeEach
    void setUp() {
        booker1 = new User();
        booker1.setName("User Name");
        booker1.setEmail("user1@mail.ru");
        userRepository.save(booker1);

        item1 = new Item();
        item1.setName("Item1 name");
        item1.setDescription("Item1 description");
        item1.setAvailable(true);
        item1.setOwnerId(2L);
        itemRepository.save(item1);

        booking1 = new Booking();
        booking1.setStart(LocalDateTime.now().plusHours(1));
        booking1.setEnd(LocalDateTime.now().plusHours(2));
        booking1.setItem(item1);
        booking1.setBooker(booker1);
        booking1.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking1);

        booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().plusHours(5));
        booking2.setEnd(LocalDateTime.now().plusHours(10));
        booking2.setItem(item1);
        booking2.setBooker(booker1);
        booking2.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking2);

    }

    @Test
    void save() {
        Booking booking = bookingRepository.findById(booking1.getId()).orElse(null);
        assertNotNull(booking);
        assertEquals(BookingStatus.WAITING, booking.getStatus());

        bookingRepository.save(BookingStatus.APPROVED, booking.getId());

        Booking updatedBooking = bookingRepository.findById(booking.getId()).orElse(null);
        assertNotNull(updatedBooking);
        assertEquals(BookingStatus.APPROVED, updatedBooking.getStatus());
    }

    @Test
    void findAllByBookerIdOrderByEndDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdOrderByEndDesc(booker1.getId(), page);
        assertEquals(2, bookings.size());
        assertTrue(bookings.get(1).getEnd().isBefore(LocalDateTime.now().plusHours(5)));
    }

    @Test
    void findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(booker1.getId(),
                LocalDateTime.now().plusHours(6), LocalDateTime.now().plusHours(7), page);
        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByBookerIdAndEndIsBeforeOrderByEndDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByEndDesc(booker1.getId(),
                LocalDateTime.now().plusHours(20), page);
        assertEquals(2, bookings.size());
    }

    @Test
    void findAllByBookerIdAndStartIsAfterOrderByEndDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStartIsAfterOrderByEndDesc(booker1.getId(),
                LocalDateTime.now().plusHours(2), page);
        assertEquals(1, bookings.size());
    }

    @Test
    void findAllByBookerIdAndStatusOrderByEndDesc() {
        List<Booking> bookings = bookingRepository.findAllByBookerIdAndStatusOrderByEndDesc(booker1.getId(),
                BookingStatus.APPROVED, page);
        assertEquals(1, bookings.size());
    }

    @Test
    void findByItemOwnerIdOrderByEndDesc() {
        List<Booking> bookings = bookingRepository.findByItemOwnerIdOrderByEndDesc(2L, page);
        assertEquals(2, bookings.size());
    }

    @Test
    void findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc() {
    }

    @Test
    void findAllByItemOwnerIdAndEndIsBeforeOrderByEndDesc() {
    }

    @Test
    void findAllByItemOwnerIdAndStartIsAfterOrderByEndDesc() {
    }

    @Test
    void findAllByItemOwnerIdAndStatusOrderByEndDesc() {
    }

    @Test
    void findAllByItemIdOrderByEndDesc() {
    }

    @Test
    void findAllByItemIdAndBookerIdAndStatusIsAndStartIsBeforeOrderByStart() {
    }

    @AfterEach
    void cleanBase() {
        bookingRepository.deleteAll();
        userRepository.deleteAll();
        itemRepository.deleteAll();
    }
}