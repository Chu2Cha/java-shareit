package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.InBookingDto;
import ru.practicum.shareit.booking.dto.ItemBookingDto;
import ru.practicum.shareit.booking.dto.OutBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    private BookingService bookingService;

    OutBookingDto outBookingDto;
    List<OutBookingDto> outBookingDtoList;

    @Autowired
    private MockMvc mvc;

    InBookingDto inBookingDto;
    private static final String BOOKER_ID = "X-Sharer-User-Id";


    @BeforeEach
    void setUp() {
        inBookingDto = new InBookingDto(LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusHours(2), 1L);
        UserDto userDto = new UserDto(1L, "Boris", "chuguevb@gamil.com");
        ItemBookingDto itemBookingDto = new ItemBookingDto(1L, userDto.getId(),
                inBookingDto.getStart(),
                inBookingDto.getEnd());
        ItemDto itemDto = new ItemDto(1L, "Item1", "Description1",
                true, null, 1L, null, null, null);

        outBookingDto = new OutBookingDto(1L,
                inBookingDto.getStart(),
                inBookingDto.getEnd(),
                BookingStatus.WAITING,
                itemDto,
                userDto);

        outBookingDtoList = new ArrayList<>();
        outBookingDtoList.add(outBookingDto);
    }

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.createBooking(any(), anyLong())).thenReturn(outBookingDto);
        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(inBookingDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(BOOKER_ID, 1))
                .andDo(result -> System.out.println("Response body: " + result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getBooking() throws Exception {
        when(bookingService.findById(anyLong(), anyLong()))
                .thenReturn(outBookingDto);
        mvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(BOOKER_ID, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(outBookingDto.getId()), Long.class));
    }

    @Test
    void getAllBookingsFromUser() throws Exception {
        when(bookingService.getAllBookingsFromUser(anyLong(),
                anyString(), anyInt(), anyInt())).thenReturn(outBookingDtoList);
        mvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(BOOKER_ID, 1))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void getAllBookingsFromOwner() throws Exception {
        when(bookingService.getAllBookingsFromOwner(anyLong(),
                anyString(), anyInt(), anyInt())).thenReturn(outBookingDtoList);
        mvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(BOOKER_ID, 1))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void update() throws Exception {
        outBookingDto.setStatus(BookingStatus.APPROVED);
        when(bookingService.approve(anyLong(), anyLong(), eq(true)))
                .thenReturn(outBookingDto);
        mvc.perform(patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(BOOKER_ID, 1)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",
                        is(BookingStatus.APPROVED.toString())));
        verify(bookingService).approve(1L, 1L, true);
    }
}