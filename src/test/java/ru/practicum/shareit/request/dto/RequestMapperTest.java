package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RequestMapperTest {

    @InjectMocks
    private RequestMapper requestMapper;

    private User requestor;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        requestor = new User();
        requestor.setId(1L);
        UserDto requestorDto = new UserDto();
        requestorDto.setId(requestor.getId());

        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(2L);
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setOwnerId(itemDto.getOwnerId());

        itemRequest = new ItemRequest(1L, "Requset", requestor,
                LocalDateTime.now(), List.of(item));
        itemRequestDto = new ItemRequestDto(itemRequest.getId(), item.getDescription(),
                requestorDto, itemRequest.getCreated(), List.of(itemDto));
    }

    @Test
    void toRequest() {
        ItemRequest newRequest = requestMapper.toRequest(itemRequestDto, requestor);
        assertEquals(newRequest.getRequestor(), requestor);
        assertEquals(newRequest.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    void toRequestDto() {
        ItemRequestDto requestDto = requestMapper.toRequestDto(itemRequest);
        assertEquals(requestDto.getDescription(), itemRequest.getDescription());
    }
}