package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService{

    private final UserService userService;

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Override
    public BookingDto createBooking(BookingDto bookingDto, long bookerId) {
        userService.findUserById(bookerId);
        User booker = userMapper.toUser(userService.findUserById(bookerId));
        Item item =  itemMapper.toItem(itemService.findById(bookingDto.getItem().getId(),bookerId));
        Booking booking = Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
        bookingRepository.save(booking);
        return bookingMapper.toBookingDto(booking);
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
