package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
@AllArgsConstructor
public class BookingMapper {

    public OutBookingDto toOutBookingDto(Booking booking) {
        return OutBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .status(booking.getStatus())
                .status(booking.getStatus())
                .build();
    }

    public ItemBookingDto toItemBookingDto(Booking booking) {
        return ItemBookingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .bookerId(booking.getBooker() != null ? booking.getBooker().getId() : null)
                .build();
    }

    public Booking toBooking(InBookingDto inBookingDto, Item item, User booker) {
        return Booking.builder()
                .start(inBookingDto.getStart())
                .end(inBookingDto.getEnd())
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }
}
