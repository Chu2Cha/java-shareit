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
    private RequestMapper requestMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestRepository requestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ItemMapper itemMapper;

    User requestor;
    UserDto requstorDto;
    ItemRequest itemRequest;
    ItemRequestDto itemRequestDto;
    Long requestorId;

    Item item;
    ItemDto itemDto;

    @BeforeEach
    void setUp() {
        requestorId = 1L;
        requestor = new User(requestorId, "Requestor", "req@mail.ru");
        requstorDto = new UserDto(requestor.getId(), requestor.getName(), requestor.getEmail());
        item = new Item(1L, "ItemName", "ItemDescription",
                true, requestorId, null, null, null);
        itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), null, requestorId, null,
                null, Collections.emptyList());
        itemRequest = new ItemRequest(1L, "Request", requestor,
                LocalDateTime.now().plusHours(1), List.of(item));
        itemRequestDto = new ItemRequestDto(itemRequest.getId(), itemRequest.getDescription(),
                requstorDto, itemRequest.getCreated(), List.of(itemDto));
    }

    @Test
    void create_whenRequestorIsFound_thenReturnSuccess() {
        when(userService.findUserById(anyLong())).thenReturn(requstorDto);
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(requestor));
        when(requestMapper.toRequest(any(), any())).thenReturn(itemRequest);
        when(requestRepository.save(any())).thenReturn(itemRequest);
        when(requestMapper.toRequestDto(any())).thenReturn(itemRequestDto);

        ItemRequestDto resultDto = requestService.create(itemRequestDto, requestorId);

        assertNotNull(resultDto);
        assertEquals(resultDto.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    void create_whenRequestorNotFound_thenReturnNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestService.create(itemRequestDto, -5L));
    }

    @Test
    void findById_whenRequestIsFound_thenSuccess() {
        when(userService.findUserById(anyLong())).thenReturn(requstorDto);
        when(requestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(requestMapper.toRequestDto(any())).thenReturn(itemRequestDto);
        when(userService.findUserById(anyLong())).thenReturn(requstorDto);
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);

        ItemRequestDto resultDto = requestService.findById(itemRequest.getId(), requestorId);
        assertNotNull(resultDto);
        assertEquals(resultDto.getRequester(), itemRequestDto.getRequester());
    }

    @Test
    void findById_whenRequestNotFound_thenReturnNotFoundException() {
        when(userService.findUserById(anyLong())).thenReturn(requstorDto);
        when(requestRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> requestService.findById(-1L, requestorId));
    }

    @Test
    void findAllFromUser() {
        when(userService.findUserById(anyLong())).thenReturn(requstorDto);
        when(requestRepository.findAllByRequesterIdOrderByCreatedDesc(anyLong()))
                .thenReturn(List.of(itemRequest));
        when(requestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(requestMapper.toRequestDto(any())).thenReturn(itemRequestDto);
        when(userService.findUserById(anyLong())).thenReturn(requstorDto);
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);
        List<ItemRequestDto> itemRequestDtoList = requestService.findAllFromUser(requestorId);

        assertEquals(1, itemRequestDtoList.size());
    }

    @Test
    void findAllRequestsForAnswer() {
        when(userService.findUserById(anyLong())).thenReturn(requstorDto);
        when(requestRepository.findAllByRequesterIdIsNot(anyLong(), any()))
                .thenReturn(List.of(itemRequest));
        when(requestRepository.findById(itemRequest.getId())).thenReturn(Optional.of(itemRequest));
        when(requestMapper.toRequestDto(any())).thenReturn(itemRequestDto);
        when(userService.findUserById(anyLong())).thenReturn(requstorDto);
        when(itemRepository.findByRequestId(anyLong())).thenReturn(List.of(item));
        when(itemMapper.toItemDto(any())).thenReturn(itemDto);
        List<ItemRequestDto> itemRequestDtoList = requestService.findAllRequestsForAnswer(requestorId, 0, 1);

        assertEquals(1, itemRequestDtoList.size());
    }
}