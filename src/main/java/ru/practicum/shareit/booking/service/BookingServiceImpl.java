package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.InBookingDto;
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final UserService userService;

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final UserMapper userMapper;
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public OutBookingDto createBooking(InBookingDto inBookingDto, long bookerId) {
        userService.findUserById(bookerId);
        User booker = userMapper.toUser(userService.findUserById(bookerId));
        if (inBookingDto.getEnd().isBefore(inBookingDto.getStart())) {
            throw new BadRequestException("Дата окончания бронирования раньше начала бронирования!");
        }
        if (inBookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата окончания бронирования раньше текущего момента!");
        }
        if (inBookingDto.getEnd().isEqual(inBookingDto.getStart())) {
            throw new BadRequestException("Время начала и окончания бронировая совпадают!");
        }
        if (inBookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Дата начала бронирования раньше текущего момента!");
        }
        Item item = itemMapper.toItem(itemService.findById(inBookingDto.getItemId(), bookerId));
        if (item.getAvailable()) {
            Booking booking = Booking.builder()
                    .start(inBookingDto.getStart())
                    .end(inBookingDto.getEnd())
                    .item(item)
                    .booker(booker)
                    .status(BookingStatus.WAITING)
                    .build();
            if(booking.getItem().getOwnerId()==bookerId){
                throw new NotFoundException("Владелец не может бронировать собственную вещь!");
            }
            bookingRepository.save(booking);
            return bookingMapper.toOutBookingDto(booking);
        } else
            throw new BadRequestException("Предмет не доступен к бронированию.");

    }

    @Override
    @Transactional
    public OutBookingDto approve(long bookingId, long bookerId, Boolean approved) {
        OutBookingDto booking = findById(bookingId, bookerId);
        if (booking.getItem().getOwnerId() != bookerId) {
            throw new NotFoundException("Только тот, кто бронирует, может менять статус.");
        }
        if(booking.getStatus()!=BookingStatus.WAITING){
            throw new BadRequestException("Статус букинга не WAITING.");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        bookingRepository.save(booking.getStatus(), bookingId);
        return booking;
    }

    @Override
    @Transactional
    public OutBookingDto deleteBooking(long bookingId, long bookerId) {
        return null;
    }

    @Override
    @Transactional
    public OutBookingDto findById(long bookingId, long bookerId) {
        Booking booking = bookingRepository.findById(bookingId).
                orElseThrow(() -> new NotFoundException("Бронирование не найдено."));
        if (booking.getBooker().getId() == bookerId || booking.getItem().getOwnerId() == bookerId) {
            return bookingMapper.toOutBookingDto(booking);
        } else {
            throw new NotFoundException("Только владелец или букинер может смотреть бронирования!");
        }
    }


    @Override
    @Transactional
    public List<OutBookingDto> getAllBookingsFromUser(long bookerId, String state) {
        userService.findUserById(bookerId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookingList;
        switch (state) {
            case "ALL":
                bookingList = bookingRepository
                        .findAllByBookerIdOrderByEndDesc(bookerId);
                break;
            case "CURRENT":
                bookingList = bookingRepository
                        .findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(bookerId, now, now);
                break;
            case "PAST":
                bookingList = bookingRepository
                        .findAllByBookerIdAndEndIsBeforeOrderByEndDesc(bookerId, now);
                break;
            case "FUTURE":
                bookingList = bookingRepository
                        .findAllByBookerIdAndStartIsAfterOrderByEndDesc(bookerId, now);
                break;
            case "WAITING":
                bookingList = bookingRepository
                        .findAllByBookerIdAndStatusOrderByEndDesc(bookerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookingList = bookingRepository
                        .findAllByBookerIdAndStatusOrderByEndDesc(bookerId, BookingStatus.REJECTED);
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");

        }
        return bookingList.stream()
                .map(bookingMapper::toOutBookingDto)
                .collect(Collectors.toList());


    }

    @Override
    public List<OutBookingDto> getAllBookingsFromOwner(long ownerId, String state) {
        userService.findUserById(ownerId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookingList;
        String query;
        switch (state) {
            case "ALL":
                bookingList= bookingRepository
                        .findByItemOwnerIdOrderByEndDesc(ownerId);
                break;
            case "CURRENT":
                bookingList = bookingRepository
                        .findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(ownerId, now, now);
                break;
            case "PAST":
                bookingList = bookingRepository
                        .findAllByItemOwnerIdAndEndIsBeforeOrderByEndDesc(ownerId, now);
                break;
            case "FUTURE":
                bookingList = bookingRepository
                        .findAllByItemOwnerIdAndStartIsAfterOrderByEndDesc(ownerId, now);
                break;
            case "WAITING":
                bookingList = bookingRepository
                        .findAllByItemOwnerIdAndStatusOrderByEndDesc(ownerId, BookingStatus.WAITING);
                break;
            case "REJECTED":
                bookingList = bookingRepository
                        .findAllByItemOwnerIdAndStatusOrderByEndDesc(ownerId, BookingStatus.REJECTED);
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");

        }
        return bookingList.stream()
                .map(bookingMapper::toOutBookingDto)
                .collect(Collectors.toList());
    }
}
