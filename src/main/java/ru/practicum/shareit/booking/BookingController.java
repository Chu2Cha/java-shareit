package ru.practicum.shareit.booking;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;


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
    public BookingDto createBooking(@Valid @RequestBody BookingDto bookingDto, @RequestHeader(BOOKER_ID) long ownerId){
        return bookingService.createBooking(bookingDto, ownerId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long id, @RequestHeader(BOOKER_ID) long bookerId) {
        bookingService.deleteBooking(id, bookerId);
    }

    @GetMapping("/{id}")
    public BookingDto getItem(@PathVariable("id") long id, @RequestHeader(BOOKER_ID) long bookerId) {
        return bookingService.findById(id, bookerId);
    }

    @PatchMapping("/{id}")
    public BookingDto update(@RequestBody BookingDto bookingDto,
                          @PathVariable("id") long id,
                          @RequestHeader(BOOKER_ID) long bookerId) {
        return bookingService.updateBooking(bookingDto, id, bookerId);
    }

}
