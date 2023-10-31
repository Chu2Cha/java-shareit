package ru.practicum.shareit.booking;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.InBookingDto;
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;


@RestController
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;
    private static final String BOOKER_ID = "X-Sharer-User-Id";


    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public OutBookingDto createBooking(@RequestBody InBookingDto inBookingDto,
                                       @RequestHeader(BOOKER_ID) long ownerId) {
        return bookingService.createBooking(inBookingDto, ownerId);
    }

    @GetMapping("/{id}")
    public OutBookingDto getBooking(@PathVariable("id") long id, @RequestHeader(BOOKER_ID) long bookerId) {
        return bookingService.findById(id, bookerId);
    }

    @GetMapping
    public List<OutBookingDto> getAllBookingsFromUser(@RequestHeader(BOOKER_ID) long bookerId,
                                                      @RequestParam(defaultValue = "ALL") String state,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size) {
        return bookingService.getAllBookingsFromUser(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<OutBookingDto> getAllBookingsFromOwner(@RequestHeader(BOOKER_ID) long ownerId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        return bookingService.getAllBookingsFromOwner(ownerId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public OutBookingDto update(@RequestHeader(BOOKER_ID) long bookerId,
                                @PathVariable long bookingId,
                                @RequestParam Boolean approved) {
        return bookingService.approve(bookingId, bookerId, approved);
    }

}
