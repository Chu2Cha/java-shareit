package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService{

    private final UserService userService;

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto createBooking(BookingDto bookingDto, long bookerId) {
        userService.findUserById(bookerId);
        Booking booking =  bookingMapper.toBooking(bookingDto);
        bookingRepository.save(booking);
        return bookingDto;
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
