package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService{
    @Override
    public BookingDto createBooking(BookingDto bookingDto, long bookerId) {
        return null;
    }

    @Override
    public BookingDto updateBooking(BookingDto bookingDto, long id, long bookerId) {
        return null;
    }

    @Override
    public BookingDto deleteBooking(long bookingId, long bookerId) {
        return null;
    }

    @Override
    public BookingDto findById(long bookingId, long bookerId) {
        return null;
    }

    @Override
    public List<BookingDto> getAllBookings(long bookerId) {
        return null;
    }
}
