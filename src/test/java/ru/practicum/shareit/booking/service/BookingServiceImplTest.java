package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

   private User user;
   private UserDto userDto;
   private Item item;
   private ItemDto itemDto;

    private long itemId;
    private long userId;

   private int from = 0;
   private int size = 10;

   private Booking booking;

   @BeforeEach
   void setUp(){
       itemId = 1L;
       userId = 2L;
       user = new User(userId, "name", "email@mail.ru");
       userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
       item = new Item(itemId, "ItemName", "ItemDescription",
               true, userId, null, null, null);
       itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(),
               item.getAvailable(), null, userId, null,
               null, Collections.emptyList());
       booking = new Booking(); //todo booking
   }


    @Test
    void createBooking() {
    }

    @Test
    void approve() {
    }

    @Test
    void findById() {
    }

    @Test
    void getAllBookingsFromUser() {
    }

    @Test
    void getAllBookingsFromOwner() {
    }
}