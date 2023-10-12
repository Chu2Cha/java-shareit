package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @InjectMocks
    private RequestServiceImpl requestService;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;
    @Mock
    private RequestMapper requestMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemMapper itemMapper;

    User requester;
    UserDto requesterDto;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;
    Long requesterId;

    Item item;
    ItemDto itemDto;

    @BeforeEach
    void setUp() {
        requesterId = 1L;
        requester = new User(requesterId, "Requester", "req@mail.ru");
        requesterDto = new UserDto(requester.getId(), requester.getName(), requester.getEmail());
        item = new Item(1L, "ItemName", "ItemDescription",
                true, requesterId, null, null, null);
        itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), null, requesterId, null,
                null, Collections.emptyList());
        itemRequest = new ItemRequest(1L, "Request", requester,
                LocalDateTime.now().plusHours(1), List.of(item));
        itemRequestDto = new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(),
                requesterDto, itemRequest.getCreated(), List.of(itemDto));
    }

    @Test
    void create_whenRequesterIsFound_thenReturnSuccess() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requester));
        when(requestMapper.toRequest(any(), any())).thenReturn(itemRequest);
        when(requestRepository.save(any())).thenReturn(itemRequest);
        when(requestMapper.toRequestDto(any())).thenReturn(itemRequestDto);

        ItemRequestDto resultDto = requestService.create(itemRequestDto, requesterId);

        assertNotNull(resultDto);
        assertEquals(resultDto.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    void create_whenRequesterNotFound_thenReturnNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.create(itemRequestDto, -5L));
    }

    @Test
    void findById_whenRequestIsFound_thenSuccess() {
        when(userService.findUserById(anyLong())).thenReturn(requesterDto);
        when(requestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(requestMapper.toRequestDto(any())).thenReturn(itemRequestDto);
        when(userService.findUserById(anyLong())).thenReturn(requesterDto);
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);
        when(userMapper.toUserDto(any())).thenReturn(requesterDto);

        ItemRequestDto resultDto = requestService.findById(itemRequest.getId(), requesterId);
        assertNotNull(resultDto);
        assertEquals(resultDto.getRequester(), itemRequestDto.getRequester());
    }

    @Test
    void findById_whenRequestNotFound_thenReturnNotFoundException() {
        when(userService.findUserById(anyLong())).thenReturn(requesterDto);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.findById(-1L, requesterId));
    }

    @Test
    void findAllFromUser() {
        when(userService.findUserById(anyLong())).thenReturn(requesterDto);
        when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(itemRequest));
        when(requestMapper.toRequestDto(any())).thenReturn(itemRequestDto);
        when(userService.findUserById(anyLong())).thenReturn(requesterDto);
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);
        List<ItemRequestDto> itemRequestDtoList = requestService.findAllFromUser(requesterId);

        assertEquals(1, itemRequestDtoList.size());
    }

    @Test
    void findAllRequestsForAnswer() {
        when(userService.findUserById(anyLong())).thenReturn(requesterDto);
        when(requestRepository.findAllByRequesterIdIsNot(anyLong(), any()))
                .thenReturn(List.of(itemRequest));
        when(requestMapper.toRequestDto(any())).thenReturn(itemRequestDto);
        when(userService.findUserById(anyLong())).thenReturn(requesterDto);
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);
        List<ItemRequestDto> itemRequestDtoList = requestService.findAllRequestsForAnswer(requesterId, 0, 1);

        assertEquals(1, itemRequestDtoList.size());
    }
}