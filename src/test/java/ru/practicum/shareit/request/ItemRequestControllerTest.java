package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@AutoConfigureMockMvc
class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @MockBean
    private RequestService requestService;

    private static final String REQUESTOR_ID = "X-Sharer-User-Id";

    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        UserDto requestor = new UserDto(1L, "Requestor", "mail@mail.ru");
        ItemDto itemDto = new ItemDto(1L, "Item",
                "ItemDescription", true, 1L, 1L,
                null, null, List.of());
        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("RequestDescription")
                .requestor(requestor)
                .created(LocalDateTime.now().plusHours(1))
                .items(List.of(itemDto))
                .build();
    }


    @Test
    void create() throws Exception {
        when(requestService.create(any(), anyLong())).thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUESTOR_ID, 1))
                .andDo(result -> System.out.println("Response body: " + result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getRequest() throws Exception {
        when(requestService.findById(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);
        mvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUESTOR_ID, 1))
                .andDo(result -> System.out.println("Response body: " + result.getResponse().getContentAsString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class));
    }

    @Test
    void getAllUserRequests() throws Exception {
        when(requestService.findAllFromUser(anyLong())).thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUESTOR_ID, 1))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void getAllRequestsForAnswer() throws Exception {
        when(requestService.findAllRequestsForAnswer(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDto));
        mvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(REQUESTOR_ID, 1))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn()
                .getResponse()
                .getContentAsString();
    }
}