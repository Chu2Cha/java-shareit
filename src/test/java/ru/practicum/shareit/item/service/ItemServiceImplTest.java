package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.repository.CommentRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingMapper bookingMapper;
    @Mock
    private CommentMapper commentMapper;
    @Mock
    private CommentRepository commentRepository;

    @Mock
    private UserMapper userMapper;
    @Mock
    private RequestMapper requestMapper;
    @Mock
    private RequestService requestService;

    private User user;
    private UserDto userDto;
    private Item item;
    private ItemDto itemDto;
    private long itemId;
    private long userId;
    private int from = 0;
    private int size = 10;

    @BeforeEach
    void beforeEach() {
        itemId = 1L;
        userId = 2L;
        user = new User(userId, "name", "email@mail.ru");
        userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
        item = new Item(itemId, "ItemName", "ItemDescription",
                true, userId, null, null, null);
        itemDto = new ItemDto(item.getId(), item.getName(), item.getDescription(),
                item.getAvailable(), null, userId, null,
                null, Collections.emptyList());
    }


    @Test
    void getAllItemsFromUser() {
        Item item2 = new Item(2L, "SecondItem",
                "SecondItemDescription", true,
                userId, null, null, null);

        Pageable page = PageRequest.of(from / size, size);
        when(itemRepository.findAllByOwnerIdOrderByIdAsc(userId, page))
                .thenReturn(Arrays.asList(item, item2));
        List<ItemDto> itemDtoList = itemService.getAllItemsFromUser(userId, from, size);
        assertEquals(2, itemDtoList.size());
        assertEquals("ItemName", itemDtoList.get(0).getName());
        assertEquals("SecondItem", itemDtoList.get(1).getName());
    }

    @Test
    void createItem_whenRequestServiceFindIsNull() {
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemRepository.save(item)).thenReturn(item);
        ItemDto actualItemDto = itemService.createItem(itemDto, userId);
        actualItemDto.setComments(Collections.emptyList());
        assertEquals(itemDto, actualItemDto);
        verify(itemRepository).save(item);
    }

    @Test
    void createItem_whenRequestServiceFindIsNotNull() {
        ItemRequest itemRequest = new ItemRequest(1L,
                "description", user, LocalDateTime.now(),
                Collections.emptyList());
        item.setRequest(itemRequest);
        itemDto.setRequestId(itemRequest.getId());
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemDto actualItemDto = itemService.createItem(itemDto, userId);
        actualItemDto.setComments(Collections.emptyList());
        assertEquals(itemDto, actualItemDto);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void updateItem_whenNewName_thenReturnItemDtoWithNewName() {
        String newName = "NewName";
        ItemDto itemDtoForUpdate = new ItemDto(itemId, newName,
                null, null, null,
                null, null, null, Collections.emptyList());
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocation -> {
                    Item savedItem = invocation.getArgument(0);
                    savedItem.setName(newName);
                    item.setName(newName);
                    return savedItem;
                });
        ItemDto updatedItemDto = itemService.updateItem(itemDtoForUpdate,
                itemId, userId);
        assertEquals(newName, updatedItemDto.getName());
        assertEquals(item.getName(), updatedItemDto.getName());
    }

    @Test
    void updateItem_whenNoNameAndFalseAvailable_thenReturnItemDtoWithOldName() {
        ItemDto itemDtoForUpdate = new ItemDto(itemId, null,
                null, false, null,
                null, null, null, Collections.emptyList());
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class)))
                .thenAnswer(invocation -> {
                    Item savedItem = invocation.getArgument(0);
                    savedItem.setAvailable(false);
                    item.setAvailable(false);
                    return savedItem;
                });
        ItemDto updatedItemDto = itemService.updateItem(itemDtoForUpdate,
                itemId, userId);
        assertEquals(false, updatedItemDto.getAvailable());
        assertEquals(item.getName(), updatedItemDto.getName());
    }

    @Test
    void findById_whenUserFoundAndItemFound_thenReturnItemDto() {
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        ItemDto resultItemDto = itemService.findById(itemId, userId);
        assertEquals(itemDto, resultItemDto);
    }

    @Test
    void findById_whenUserFoundAndItemNotFound_thenReturnNotFoundException() {
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.findById(itemId, userId));
        verify(itemRepository).findById(itemId);
    }

    @Test
    void deleteItem_whenUserFoundAndItemFound_thenSuccess() {
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        itemService.deleteItem(itemId, userId);
        verify(itemRepository, Mockito.times(1))
                .deleteById(itemId);
    }

    @Test
    void searchItems_whenEmptyMessage_thenReturnEmptyList() {
        when(userService.findUserById(userId)).thenReturn(userDto);
        Pageable page = PageRequest.of(from / size, size);
        List<ItemDto> emptyList = itemService.searchItems(" ", userId,
                from, size);
        assertEquals(emptyList.size(), 0);
    }

    @Test
    void searchItems_whenMessageNotEmptyAndFoundAnything() {
        Pageable page = PageRequest.of(from / size, size);
        String searchText = "name";
        List<Item> itemList = new ArrayList<>();
        itemList.add(item);
        when(userService.findUserById(userId)).thenReturn(userDto);
        when(itemRepository.searchItems(searchText, page))
                .thenReturn(itemList);
        List<ItemDto> emptyList = itemService.searchItems("name", userId,
                from, size);
        assertEquals(emptyList.size(), 1);
    }

    @Test
    void addComment_whenCommentIsEmpty_thenBadRequestException() {
        CommentDto commentDto = new CommentDto(1L, "",
                "user", LocalDateTime.now());
        assertThrows(BadRequestException.class, () ->
                itemService.addComment(itemId, userId, commentDto));
    }

//    @Test
//    void addComment_whenNormalCommentAndBookingWasCanceled_thenReturnComment(){
//        Comment comment = new Comment();
//        CommentDto commentDto = new CommentDto(1L, "new comment",
//                "user", LocalDateTime.now());
//        when(userService.findUserById(userId)).thenReturn(userDto);
//        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
//        when(bookingRepository.findAllByItemIdAndBookerIdAndStatusIsAndStartIsBeforeOrderByStart(itemId,
//                userId, BookingStatus.CANCELED, LocalDateTime.now() )).thenReturn(comment);
//    }


}