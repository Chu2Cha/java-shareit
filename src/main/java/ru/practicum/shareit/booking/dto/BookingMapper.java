package ru.practicum.shareit.booking.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;

@Component
public class BookingMapper {



    public OutBookingDto toOutBookingDto(Booking booking) {
        return OutBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .item(booking.getItem())
                .booker(booking.getBooker())
                .status(booking.getStatus())
                .build();
    }

    public Booking toBooking(OutBookingDto outBookingDto) {
        return Booking.builder()
                .id(outBookingDto.getId())
                .start(outBookingDto.getStart())
                .end(outBookingDto.getEnd())
                .status(outBookingDto.getStatus())
                .build();
    }
}
