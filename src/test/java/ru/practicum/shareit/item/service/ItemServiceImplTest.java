package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;

    @Mock
    private ItemMapper itemMapper;
    @Mock
    private  UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private  ItemRepository itemRepository;
    @Mock
    private  BookingRepository bookingRepository;
    @Mock
    private  BookingMapper bookingMapper;
    @Mock
    private  CommentMapper commentMapper;
    @Mock
    private  CommentRepository commentRepository;

    @Mock
    private  UserMapper userMapper;
    @Mock
    private  RequestMapper requestMapper;
    @Mock
    private  RequestService requestService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }
    @Test
    void getAllItemsFromUser() {
    }

    @Test
    void createItem() {
    }

    @Test
    void updateItem() {
    }

    @Test
    void findById_whenUserFoundAndItemFound_thenReturnItemDto() {
        long itemId = 1L;
        long userId = 2L;
        User user = new User();
        user.setId(userId);
        Item item = new Item(itemId, "ItemName", "ItemDescription", true,
                userId, null, null, null);
        UserDto expectedUserDto = new UserDto();
        expectedUserDto.setId(userId);
        ItemDto expectedItemDto = new ItemDto();
        expectedItemDto.setId(itemId);
        expectedItemDto.setOwnerId(userId);
        expectedItemDto.setName(item.getName());
        expectedItemDto.setDescription(item.getDescription());
        expectedItemDto.setAvailable(item.getAvailable());
        expectedItemDto.setComments(Collections.emptyList());

        when(userService.findUserById(anyLong())).thenReturn(expectedUserDto);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemMapper.toItemDto(any())).thenReturn(expectedItemDto);



        ItemDto actualItemDto = itemService.findById(itemId, userId);

        assertEquals(expectedItemDto, actualItemDto);
        verify(userService).findUserById(userId);
        verify(itemRepository).findById(itemId);
        verify(itemMapper).toItemDto(item);


    }

    @Test
    void findById_whenUserNotFound_thenReturnNotFoundException() {

    }

    @Test
    void findById_whenUserFoundAndItemNotFound_thenReturnNotFoundException() {

    }

    @Test
    void deleteItem() {
    }

    @Test
    void searchItems() {
    }

    @Test
    void addComment() {
    }
}