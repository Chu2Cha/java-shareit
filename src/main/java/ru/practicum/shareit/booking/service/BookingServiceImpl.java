package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
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
    private final ItemMapper itemMapper;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public OutBookingDto createBooking(InBookingDto inBookingDto, long bookerId) {
        userService.findUserById(bookerId);
        User booker = userRepository.findById(bookerId).orElseThrow(
                () -> new NotFoundException("Пользователь с id " + bookerId + " не найден."));
        if (inBookingDto.getEnd().isBefore(inBookingDto.getStart())) {
            throw new BadRequestException("Дата окончания бронирования раньше начала бронирования!");
        }
        if (inBookingDto.getEnd().isEqual(inBookingDto.getStart())) {
            throw new BadRequestException("Время начала и окончания бронировая совпадают!");
        }
        Item item = itemRepository.findById(inBookingDto.getItemId()).orElseThrow(
                () -> new NotFoundException("Предмет с id " + inBookingDto.getItemId() + " не найден."));
        if (item.getOwnerId() == bookerId) {
            throw new NotFoundException("Владелец не может бронировать собственную вещь!");
        }
        if (item.getAvailable()) {
            Booking booking = bookingMapper.toBooking(inBookingDto, item, booker);
            bookingRepository.save(booking);
            return buildOutBookingDto(booking);
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
        if (booking.getStatus() != BookingStatus.WAITING) {
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
    public OutBookingDto findById(long bookingId, long bookerId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено."));
        if (booking.getBooker().getId() == bookerId || booking.getItem().getOwnerId() == bookerId) {
            return buildOutBookingDto(booking);
        } else {
            throw new NotFoundException("Только владелец или букинер может смотреть бронирования!");
        }
    }


    @Override
    @Transactional
    public List<OutBookingDto> getAllBookingsFromUser(long bookerId, String state, int from, int size) {
        userService.findUserById(bookerId);
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookingList;
        Pageable page = PageRequest.of(from / size, size);
        switch (state) {
            case "ALL":
                bookingList = bookingRepository
                        .findAllByBookerIdOrderByEndDesc(bookerId, page);
                break;
            case "CURRENT":
                bookingList = bookingRepository
                        .findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(bookerId, now, now, page);
                break;
            case "PAST":
                bookingList = bookingRepository
                        .findAllByBookerIdAndEndIsBeforeOrderByEndDesc(bookerId, now, page);
                break;
            case "FUTURE":
                bookingList = bookingRepository
                        .findAllByBookerIdAndStartIsAfterOrderByEndDesc(bookerId, now, page);
                break;
            case "WAITING":
                bookingList = bookingRepository
                        .findAllByBookerIdAndStatusOrderByEndDesc(bookerId, BookingStatus.WAITING, page);
                break;
            case "REJECTED":
                bookingList = bookingRepository
                        .findAllByBookerIdAndStatusOrderByEndDesc(bookerId, BookingStatus.REJECTED, page);
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");

        }
        return bookingList.stream()
                .map(this::buildOutBookingDto)
                .collect(Collectors.toList());


    }

    @Override
    public List<OutBookingDto> getAllBookingsFromOwner(long ownerId, String state, int from, int size) {
        userService.findUserById(ownerId);
        LocalDateTime now = LocalDateTime.now();
        Pageable page = PageRequest.of(from / size, size);
        List<Booking> bookingList;
        switch (state) {
            case "ALL":
                bookingList = bookingRepository
                        .findByItemOwnerIdOrderByEndDesc(ownerId, page);
                break;
            case "CURRENT":
                bookingList = bookingRepository
                        .findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(ownerId, now, now, page);
                break;
            case "PAST":
                bookingList = bookingRepository
                        .findAllByItemOwnerIdAndEndIsBeforeOrderByEndDesc(ownerId, now, page);
                break;
            case "FUTURE":
                bookingList = bookingRepository
                        .findAllByItemOwnerIdAndStartIsAfterOrderByEndDesc(ownerId, now, page);
                break;
            case "WAITING":
                bookingList = bookingRepository
                        .findAllByItemOwnerIdAndStatusOrderByEndDesc(ownerId, BookingStatus.WAITING, page);
                break;
            case "REJECTED":
                bookingList = bookingRepository
                        .findAllByItemOwnerIdAndStatusOrderByEndDesc(ownerId, BookingStatus.REJECTED, page);
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");

        }
        return bookingList.stream()
                .map(this::buildOutBookingDto)
                .collect(Collectors.toList());
    }

    private OutBookingDto buildOutBookingDto(Booking booking) {
        OutBookingDto outBookingDto = bookingMapper.toOutBookingDto(booking);
        outBookingDto.setBooker(userMapper.toUserDto(booking.getBooker()));
        outBookingDto.setItem(itemMapper.toItemDto(booking.getItem()));
        return outBookingDto;
    }
}
