package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingDto bookingDto, long bookerId);
    BookingDto updateBooking (BookingDto bookingDto, long id, long bookerId);

    BookingDto deleteBooking(long bookingId, long bookerId);

    BookingDto findById(long bookingId, long bookerId);

    List<BookingDto> getAllBookings(long bookerId);

}
