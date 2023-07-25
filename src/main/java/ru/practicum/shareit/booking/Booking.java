package ru.practicum.shareit.booking;

import lombok.Data;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Date;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class Booking {
    private int id;
    private Date start;
    private Date end;
    private Item item;
    private User booker;
    private Status status;

    private enum Status {
        WAITING,
        APPROVED,
        REJECTED,
        CANCELED
    }
}
