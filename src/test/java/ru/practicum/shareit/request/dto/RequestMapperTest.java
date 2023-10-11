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

    private User requester;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        requester = new User();
        requester.setId(1L);
        UserDto requesterDto = new UserDto();
        requesterDto.setId(requester.getId());

        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(2L);
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setOwnerId(itemDto.getOwnerId());

        itemRequest = new ItemRequest(1L, "Request", requester,
                LocalDateTime.now(), List.of(item));
        itemRequestDto = new ItemRequestDto(itemRequest.getId(), item.getDescription(),
                requesterDto, itemRequest.getCreated(), List.of(itemDto));
    }

    @Test
    void toRequest() {
        ItemRequest newRequest = requestMapper.toRequest(itemRequestDto, requester);
        assertEquals(newRequest.getRequester(), requester);
        assertEquals(newRequest.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    void toRequestDto() {
        ItemRequestDto requestDto = requestMapper.toRequestDto(itemRequest);
        assertEquals(requestDto.getDescription(), itemRequest.getDescription());
    }
}