package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.InBookingDto;
import ru.practicum.shareit.booking.dto.OutBookingDto;

import java.util.List;

public interface BookingService {

    OutBookingDto createBooking(InBookingDto inBookingDto, long bookerId);

    OutBookingDto approve(long bookingId, long bookerId, Boolean approved);

    OutBookingDto deleteBooking(long bookingId, long bookerId);

    OutBookingDto findById(long bookingId, long bookerId);

    List<OutBookingDto> getAllBookingsFromUser(long bookerId, String state);

    List<OutBookingDto> getAllBookingsFromOwner(long ownerId, String state);
}
