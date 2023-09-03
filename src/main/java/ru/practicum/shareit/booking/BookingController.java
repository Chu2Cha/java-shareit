package ru.practicum.shareit.booking;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.InBookingDto;
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
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
    public OutBookingDto createBooking(@Valid @RequestBody InBookingDto inBookingDto,
                                       @RequestHeader(BOOKER_ID) long ownerId) {
        return bookingService.createBooking(inBookingDto, ownerId);
    }

    @GetMapping("/{id}")
    public OutBookingDto getBooking(@PathVariable("id") long id, @RequestHeader(BOOKER_ID) long bookerId) {
        return bookingService.findById(id, bookerId);
    }

    @GetMapping
    public List<OutBookingDto> getAllBookingsFromUser(@RequestHeader(BOOKER_ID) long bookerId,
                                                      @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsFromUser(bookerId, state);
    }

    @GetMapping("/owner")
    public List<OutBookingDto> getAllBookingsFromOwner(@RequestHeader(BOOKER_ID) long ownerId,
                                                       @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getAllBookingsFromOwner(ownerId, state);
    }

    @PatchMapping("/{bookingId}")
    public OutBookingDto update(@RequestHeader(BOOKER_ID) long bookerId,
                                @PathVariable long bookingId,
                                @RequestParam Boolean approved) {
        return bookingService.approve(bookingId, bookerId, approved);
    }

}
