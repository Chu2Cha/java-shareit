package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.InBookingDto;
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;

    @Mock
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemMapper itemMapper;

    private User booker;
    private UserDto bookerDto;

    private Item item;
    private ItemDto itemDto;

    private long itemId;
    private long bookerId;
    private long ownerId;

    private final int from = 0;
    private final int size = 10;

    private Booking booking;


    @BeforeEach
    void setUp() {
        itemId = 1L;
        ownerId = 1L;
        bookerId = 2L;
        booker = new User(bookerId, "name", "email@mail.ru");
        bookerDto = new UserDto(booker.getId(), booker.getName(), booker.getEmail());
        item = new Item(itemId, "ItemName", "ItemDescription",
                true, ownerId, null, null, null);
        itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), null, ownerId, null,
                null, Collections.emptyList());
        booking = new Booking();
    }

    @Test
    void createBooking_whenInBookingDtoIsCorrect_thenReturnCorrectOutBookingDto() {
        InBookingDto inBookingDto = new InBookingDto(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), itemId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingMapper.toBooking(inBookingDto, item, booker)).thenReturn(booking);
        when(bookingMapper.toOutBookingDto(any())).thenReturn(OutBookingDto.builder()
                .start(inBookingDto.getStart())
                .end(inBookingDto.getEnd())
                .build());
        when(userMapper.toUserDto(any())).thenReturn(bookerDto);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        OutBookingDto outBookingDto = bookingService.createBooking(inBookingDto, bookerId);

        assertNotNull(outBookingDto);
        assertEquals(itemDto, outBookingDto.getItem());
        assertEquals(bookerDto, outBookingDto.getBooker());
        assertEquals(inBookingDto.getStart(), outBookingDto.getStart());
        assertEquals(inBookingDto.getEnd(), outBookingDto.getEnd());
    }

    @Test
    void createBooking_whenUserIsNotFound_thenReturnNotFoundException() {
        InBookingDto inBookingDto = new InBookingDto(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), itemId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(inBookingDto, 5L));
    }

    @Test
    void createBooking_whenEndIsBeforeStart_thenReturnBadRequestException() {
        InBookingDto inBookingDto = new InBookingDto(LocalDateTime.now().plusHours(3),
                LocalDateTime.now().plusHours(2), itemId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        assertThrows(BadRequestException.class, () -> bookingService.createBooking(inBookingDto, bookerId));
    }

    @Test
    void createBooking_whenEndEqualsStart_thenReturnBadRequestException() {
        InBookingDto inBookingDto = new InBookingDto(LocalDateTime.now().plusHours(2),
                LocalDateTime.now().plusHours(2), itemId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        assertThrows(BadRequestException.class, () -> bookingService.createBooking(inBookingDto, bookerId));
    }

    @Test
    void createBooking_whenItemIdIsNotFound_thenReturnNotFoundException() {
        InBookingDto inBookingDto = new InBookingDto(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), 5L);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(inBookingDto, bookerId));
    }

    @Test
    void createBooking_whenBookerIsOwner_thenReturnNotFoundException() {
        InBookingDto inBookingDto = new InBookingDto(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), itemId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> bookingService.createBooking(inBookingDto, ownerId));
    }

    @Test
    void createBooking_whenBookingIsNotAvailable_thenReturnBadRequestException() {
        item.setAvailable(false);
        InBookingDto inBookingDto = new InBookingDto(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), itemId);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        assertThrows(BadRequestException.class, () -> bookingService.createBooking(inBookingDto, bookerId));
    }

    @Test
    void approve_whenBookerIsCorrectAndAvailableIsTrueAndStatusIsWaiting_thenReturn_APPROVED() {
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        InBookingDto inBookingDto = new InBookingDto(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), itemId);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.toOutBookingDto(any())).thenReturn(OutBookingDto.builder()
                .start(inBookingDto.getStart())
                .end(inBookingDto.getEnd())
                .status(BookingStatus.WAITING)
                .build());
        when(userMapper.toUserDto(any())).thenReturn(bookerDto);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        OutBookingDto newOutBookingDto = bookingService.approve(booking.getId(), ownerId, true);
        assertEquals(BookingStatus.APPROVED, newOutBookingDto.getStatus());
    }

    @Test
    void approve_whenBookerIsCorrectAndAvailableIsFalseAndStatusIsWaiting_thenReturn_REJECTED() {
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        InBookingDto inBookingDto = new InBookingDto(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), itemId);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.toOutBookingDto(any())).thenReturn(OutBookingDto.builder()
                .start(inBookingDto.getStart())
                .end(inBookingDto.getEnd())
                .status(BookingStatus.WAITING)
                .build());
        when(userMapper.toUserDto(any())).thenReturn(bookerDto);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        OutBookingDto newOutBookingDto = bookingService.approve(booking.getId(),
                ownerId, false);
        assertEquals(BookingStatus.REJECTED, newOutBookingDto.getStatus());
    }

    @Test
    void approve_whenBookerIsCorrectAndStatusIsNotWaiting_thenBadRequestException() {
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        InBookingDto inBookingDto = new InBookingDto(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), itemId);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.toOutBookingDto(any())).thenReturn(OutBookingDto.builder()
                .start(inBookingDto.getStart())
                .end(inBookingDto.getEnd())
                .status(BookingStatus.REJECTED)
                .build());
        when(userMapper.toUserDto(any())).thenReturn(bookerDto);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        assertThrows(BadRequestException.class, () ->
                bookingService.approve(booking.getId(),
                        ownerId, true));
    }

    @Test
    void approve_whenBookerIsNotCorrect_thenNotFoundException() {
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        InBookingDto inBookingDto = new InBookingDto(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), itemId);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.toOutBookingDto(any())).thenReturn(OutBookingDto.builder()
                .start(inBookingDto.getStart())
                .end(inBookingDto.getEnd())
                .status(BookingStatus.WAITING)
                .build());
        when(userMapper.toUserDto(any())).thenReturn(bookerDto);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        assertThrows(NotFoundException.class, () ->
                bookingService.approve(booking.getId(),
                        booker.getId(), true));
    }

    @Test
    void findById_whenBookerIDIsCorrect_thenReturnOutBookingDto() {
        booking.setId(1L);
        booking.setBooker(booker);
        InBookingDto inBookingDto = new InBookingDto(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), itemId);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(bookingMapper.toOutBookingDto(any())).thenReturn(OutBookingDto.builder()
                .start(inBookingDto.getStart())
                .end(inBookingDto.getEnd())
                .build());
        when(userMapper.toUserDto(any())).thenReturn(bookerDto);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        OutBookingDto outBookingDto = bookingService.findById(booking.getId(), bookerId);
        assertNotNull(outBookingDto);
    }

    @Test
    void findById_whenBookerIDIsNotCorrect_thenReturnONotFoundException() {
        User wrongUser = new User();
        wrongUser.setId(100L);
        booking.setId(1L);
        booking.setBooker(booker);
        booking.setItem(item);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        assertThrows(NotFoundException.class, () ->
                bookingService.findById(booking.getId(), wrongUser.getId()));
    }

    @Test
    void findById_whenBookerIDIsNotFound_thenReturnONotFoundException() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () ->
                bookingService.findById(100L, 100L));
    }

    @Test
    void getAllBookingsFromUser_whereStateIs_ALL() {
        when(bookingRepository.findAllByBookerIdOrderByEndDesc(anyLong(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toOutBookingDto(any())).thenReturn(OutBookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(BookingStatus.WAITING)
                .build());
        when(userMapper.toUserDto(any())).thenReturn(bookerDto);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        List<OutBookingDto> outBookingDtoList = bookingService.getAllBookingsFromUser(bookerId,
                "ALL", from, size);
        assertEquals(1, outBookingDtoList.size());
    }

    @Test
    void getAllBookingsFromUser_whereStateIs_CURRENT() {
        when(bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(
                anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toOutBookingDto(any())).thenReturn(OutBookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(BookingStatus.WAITING)
                .build());
        when(userMapper.toUserDto(any())).thenReturn(bookerDto);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        List<OutBookingDto> outBookingDtoList = bookingService.getAllBookingsFromUser(bookerId,
                "CURRENT", from, size);
        assertEquals(1, outBookingDtoList.size());
    }

    @Test
    void getAllBookingsFromUser_whereStateIs_PAST() {
        when(bookingRepository.findAllByBookerIdAndEndIsBeforeOrderByEndDesc(
                anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toOutBookingDto(any())).thenReturn(OutBookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(BookingStatus.WAITING)
                .build());
        when(userMapper.toUserDto(any())).thenReturn(bookerDto);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        List<OutBookingDto> outBookingDtoList = bookingService.getAllBookingsFromUser(bookerId,
                "PAST", from, size);
        assertEquals(1, outBookingDtoList.size());
    }

    @Test
    void getAllBookingsFromUser_whereStateIs_FUTURE() {
        when(bookingRepository.findAllByBookerIdAndStartIsAfterOrderByEndDesc(
                anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toOutBookingDto(any())).thenReturn(OutBookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(BookingStatus.WAITING)
                .build());
        when(userMapper.toUserDto(any())).thenReturn(bookerDto);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        List<OutBookingDto> outBookingDtoList = bookingService.getAllBookingsFromUser(bookerId,
                "FUTURE", from, size);
        assertEquals(1, outBookingDtoList.size());
    }

    @Test
    void getAllBookingsFromUser_whereStateIs_WAITING() {
        when(bookingRepository.findAllByBookerIdAndStatusOrderByEndDesc(
                anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toOutBookingDto(any())).thenReturn(OutBookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(BookingStatus.WAITING)
                .build());
        when(userMapper.toUserDto(any())).thenReturn(bookerDto);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        List<OutBookingDto> outBookingDtoList = bookingService.getAllBookingsFromUser(bookerId,
                "WAITING", from, size);
        assertEquals(1, outBookingDtoList.size());
    }

    @Test
    void getAllBookingsFromUser_whereStateIs_REJECTED() {
        when(bookingRepository.findAllByBookerIdAndStatusOrderByEndDesc(
                anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toOutBookingDto(any())).thenReturn(OutBookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(BookingStatus.REJECTED)
                .build());
        when(userMapper.toUserDto(any())).thenReturn(bookerDto);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        List<OutBookingDto> outBookingDtoList = bookingService.getAllBookingsFromUser(bookerId,
                "REJECTED", from, size);
        assertEquals(1, outBookingDtoList.size());
    }

    @Test
    void getAllBookingsFromUser_whereStateIs_UNSUPPORTED() {
        assertThrows(BadRequestException.class, () ->
                bookingService.getAllBookingsFromUser(bookerId,
                        "INCORRECT STATUS", from, size));
    }


    @Test
    void getAllBookingsFromOwner_whereStateIs_ALL() {
        when(bookingRepository.findByItemOwnerIdOrderByEndDesc(anyLong(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toOutBookingDto(any())).thenReturn(OutBookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(BookingStatus.WAITING)
                .build());
        when(userMapper.toUserDto(any())).thenReturn(bookerDto);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        List<OutBookingDto> outBookingDtoList = bookingService.getAllBookingsFromOwner(bookerId,
                "ALL", from, size);
        assertEquals(1, outBookingDtoList.size());
    }

    @Test
    void getAllBookingsFromOwner_whereStateIs_CURRENT() {
        when(bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByEndDesc(
                anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toOutBookingDto(any())).thenReturn(OutBookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(BookingStatus.WAITING)
                .build());
        when(userMapper.toUserDto(any())).thenReturn(bookerDto);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        List<OutBookingDto> outBookingDtoList = bookingService.getAllBookingsFromOwner(bookerId,
                "CURRENT", from, size);
        assertEquals(1, outBookingDtoList.size());
    }

    @Test
    void getAllBookingsFromOwner_whereStateIs_PAST() {
        when(bookingRepository.findAllByItemOwnerIdAndEndIsBeforeOrderByEndDesc(
                anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toOutBookingDto(any())).thenReturn(OutBookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(BookingStatus.WAITING)
                .build());
        when(userMapper.toUserDto(any())).thenReturn(bookerDto);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        List<OutBookingDto> outBookingDtoList = bookingService.getAllBookingsFromOwner(bookerId,
                "PAST", from, size);
        assertEquals(1, outBookingDtoList.size());
    }

    @Test
    void getAllBookingsFromOwner_whereStateIs_FUTURE() {
        when(bookingRepository.findAllByItemOwnerIdAndStartIsAfterOrderByEndDesc(
                anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toOutBookingDto(any())).thenReturn(OutBookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(BookingStatus.WAITING)
                .build());
        when(userMapper.toUserDto(any())).thenReturn(bookerDto);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        List<OutBookingDto> outBookingDtoList = bookingService.getAllBookingsFromOwner(bookerId,
                "FUTURE", from, size);
        assertEquals(1, outBookingDtoList.size());
    }

    @Test
    void getAllBookingsFromOwner_whereStateIs_WAITING() {
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByEndDesc(
                anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toOutBookingDto(any())).thenReturn(OutBookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(BookingStatus.WAITING)
                .build());
        when(userMapper.toUserDto(any())).thenReturn(bookerDto);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        List<OutBookingDto> outBookingDtoList = bookingService.getAllBookingsFromOwner(bookerId,
                "WAITING", from, size);
        assertEquals(1, outBookingDtoList.size());
    }

    @Test
    void getAllBookingsFromOwner_whereStateIs_REJECTED() {
        when(bookingRepository.findAllByItemOwnerIdAndStatusOrderByEndDesc(
                anyLong(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingMapper.toOutBookingDto(any())).thenReturn(OutBookingDto.builder()
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(BookingStatus.REJECTED)
                .build());
        when(userMapper.toUserDto(any())).thenReturn(bookerDto);
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        List<OutBookingDto> outBookingDtoList = bookingService.getAllBookingsFromOwner(bookerId,
                "REJECTED", from, size);
        assertEquals(1, outBookingDtoList.size());
    }

    @Test
    void getAllBookingsFromOwner_whereStateIs_UNSUPPORTED() {
        assertThrows(BadRequestException.class, () ->
                bookingService.getAllBookingsFromOwner(bookerId,
                        "INCORRECT STATUS", from, size));
    }

}