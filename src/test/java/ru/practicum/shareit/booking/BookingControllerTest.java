package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.InBookingDto;
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    private final OutBookingDto bookingDto = new OutBookingDto
            (1L,
                    LocalDateTime.now(),
                    LocalDateTime.now().plusHours(2),
                    BookingStatus.APPROVED,
                    null,
                    null);

    @Autowired
    private MockMvc mvc;

    @Test
    void createBooking() {
        when(bookingService.createBooking(any(), any())).thenReturn(bookingDto);
    }

    @Test
    void getBooking() {
    }

    @Test
    void getAllBookingsFromUser() {
    }

    @Test
    void getAllBookingsFromOwner() {
    }

    @Test
    void update() {
    }
}