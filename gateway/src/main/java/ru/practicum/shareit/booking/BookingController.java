package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.booking.dto.InBookingDto;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.exceptions.BadRequestException;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@Validated @RequestBody InBookingDto inBookingDto,
                                                @RequestHeader(Constants.USER_ID) long ownerId) {
        if (inBookingDto.getEnd().isBefore(inBookingDto.getStart())) {
            throw new BadRequestException("Дата окончания бронирования раньше начала бронирования!");
        }
        if (inBookingDto.getEnd().isEqual(inBookingDto.getStart())) {
            throw new BadRequestException("Время начала и окончания бронировая совпадают!");
        }

        log.info("Create booking {}, userId={}", inBookingDto, ownerId);

        return bookingClient.createBooking(inBookingDto, ownerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBooking(@PathVariable("id") long id, @RequestHeader(Constants.USER_ID) long bookerId) {
        return bookingClient.getBookingById(id, bookerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllBookingsFromUser(@RequestHeader(Constants.USER_ID) long userId,
                                                         @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                         @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                         @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new BadRequestException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getAllBookingsFromUser(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllBookingsFromOwner(@RequestHeader(Constants.USER_ID) long ownerId,
                                                          @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                                          @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                          @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
        log.info("Get booking with state {}, ownerId={}, from={}, size={}", stateParam, ownerId, from, size);
        return bookingClient.getAllBookingsFromOwner(ownerId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader(Constants.USER_ID) long ownerId,
                                                @PathVariable long bookingId,
                                                @RequestParam Boolean approved) {
        return bookingClient.updateBooking(bookingId, ownerId, approved);
    }
}