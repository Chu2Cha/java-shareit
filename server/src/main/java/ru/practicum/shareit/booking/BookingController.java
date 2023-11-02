package ru.practicum.shareit.booking;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.InBookingDto;
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.constants.Constants;

import java.util.List;


@RestController
@Validated
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public OutBookingDto createBooking(@RequestBody InBookingDto inBookingDto,
                                       @RequestHeader(Constants.USER_ID) long ownerId) {
        return bookingService.createBooking(inBookingDto, ownerId);
    }

    @GetMapping("/{id}")
    public OutBookingDto getBooking(@PathVariable("id") long id, @RequestHeader(Constants.USER_ID) long bookerId) {
        return bookingService.findById(id, bookerId);
    }

    @GetMapping
    public List<OutBookingDto> getAllBookingsFromUser(@RequestHeader(Constants.USER_ID) long bookerId,
                                                      @RequestParam(defaultValue = "ALL") String state,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size) {
        return bookingService.getAllBookingsFromUser(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<OutBookingDto> getAllBookingsFromOwner(@RequestHeader(Constants.USER_ID) long ownerId,
                                                       @RequestParam(defaultValue = "ALL") String state,
                                                       @RequestParam(defaultValue = "0") int from,
                                                       @RequestParam(defaultValue = "10") int size) {
        return bookingService.getAllBookingsFromOwner(ownerId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public OutBookingDto update(@RequestHeader(Constants.USER_ID) long ownerId,
                                @PathVariable long bookingId,
                                @RequestParam Boolean approved) {
        return bookingService.approve(ownerId, bookingId, approved);
    }

}
