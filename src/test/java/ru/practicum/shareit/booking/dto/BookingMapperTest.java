package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
class BookingMapperTest {

    @InjectMocks
    private BookingMapper bookingMapper;

    private User booker;
    private Item item;

    private Booking booking;

    private InBookingDto inBookingDto;

    @BeforeEach
    void setUp() {
        booker = new User();
        booker.setId(1L);
        item = new Item();
        item.setId(1L);
        item.setOwnerId(2L);
        booking = new Booking();
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        inBookingDto = new InBookingDto(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), item.getId());
    }

    @Test
    void toOutBookingDto() {
        OutBookingDto outBookingDto = bookingMapper.toOutBookingDto(booking);
        assertEquals(outBookingDto.getId(), booking.getId());
    }

    @Test
    void toItemBookingDto_whenBookerIsNotNull() {
        ItemBookingDto itemBookingDto = bookingMapper.toItemBookingDto(booking);
        assertEquals(itemBookingDto.getBookerId(), booking.getBooker().getId());
    }

    @Test
    void toItemBookingDto_whenBookerIsNull() {
        booking.setBooker(null);
        ItemBookingDto itemBookingDto = bookingMapper.toItemBookingDto(booking);
        assertNull(itemBookingDto.getBookerId());
    }

    @Test
    void toBooking() {
        Booking newBooking = bookingMapper.toBooking(inBookingDto, item, booker);
        assertEquals(BookingStatus.WAITING, newBooking.getStatus());
    }
}