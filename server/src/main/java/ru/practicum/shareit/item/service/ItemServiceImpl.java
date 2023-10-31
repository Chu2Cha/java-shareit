package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.model.Comment;
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
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper = new ItemMapper();
    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;
    private final CommentRepository commentRepository;
    private final UserMapper userMapper;
    private final RequestMapper requestMapper;
    private final RequestService requestService;


    @Override
    public List<ItemDto> getAllItemsFromUser(long ownerId, int from, int size) {
        Pageable page = PageRequest.of(from / size, size);
        List<ItemDto> itemDtoList = itemRepository.findAllByOwnerIdOrderByIdAsc(ownerId, page).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        for (ItemDto itemDto : itemDtoList) {
            updateBookings(itemDto);
            makeCommentDtoList(itemDto);
        }
        return itemDtoList;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long ownerId) {
        User owner = userMapper.toUser(userService.findUserById(ownerId));
        Item item = itemMapper.toItem(itemDto);
        item.setOwnerId(ownerId);
        item.setRequest(itemDto.getRequestId() != null ?
                requestMapper.toRequest(requestService.findById(itemDto.getRequestId(), ownerId), owner) : null);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto updateItem(ItemDto itemDto, long id, long ownerId) {
        Item itemFromStorage = itemOwnerValidation(id, ownerId);
        itemDto.setId(id);
        if (itemDto.getName() == null) {
            itemDto.setName(itemFromStorage.getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(itemFromStorage.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(itemFromStorage.getAvailable());
        }
        Item item = itemMapper.toItem(itemDto);
        item.setOwnerId(ownerId);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto findById(long id, long userId) {
        UserDto userDto = userService.findUserById(userId);  // выбрасывает ошибку, если неавторизованный пользователь зайдет.
        ItemDto itemDto = itemMapper.toItemDto(itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + id + " не найден.")));
        if (itemDto.getOwnerId() == userDto.getId()) {
            updateBookings(itemDto);
        }
        makeCommentDtoList(itemDto);
        return itemDto;
    }

    private void makeCommentDtoList(ItemDto itemDto) {
        List<Comment> comments = commentRepository.findAllByItemId(itemDto.getId());
        List<CommentDto> commentDtoList = comments.stream().map(commentMapper::toCommentDto).collect(Collectors.toList());
        itemDto.setComments(commentDtoList);
    }

    @Override
    public void deleteItem(long id, long ownerId) {
        itemOwnerValidation(id, ownerId);
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> searchItems(String message, long ownerId, int from, int size) {
        userService.findUserById(ownerId);
        Pageable page = PageRequest.of(from / size, size);
        if (checkEmptyMessage(message)) {
            return Collections.emptyList();
        }
        return itemRepository.searchItems(message, page).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto addComment(long itemId, long userId, CommentDto commentDto) {
        if (commentDto.getText().isEmpty()) {
            throw new BadRequestException("Ошибка: пустой комментарий!");
        }
        Item item = itemMapper.toItem(findById(itemId, userId));
        User author = userMapper.toUser(userService.findUserById(userId));
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookingsApproved = bookingRepository
                .findAllByItemIdAndBookerIdAndStatusIsAndStartIsBeforeOrderByStart(itemId,
                        userId, BookingStatus.APPROVED, now);
        List<Booking> bookingsPast = bookingRepository
                .findAllByItemIdAndBookerIdAndStatusIsAndStartIsBeforeOrderByStart(itemId,
                        userId, BookingStatus.CANCELED, now);
        List<Booking> bookings = Stream.concat(bookingsApproved.stream(), bookingsPast.stream())
                .collect(Collectors.toList());
        if (!bookings.isEmpty()) {
            Comment comment = commentMapper.toComment(commentDto);
            comment.setAuthor(author);
            comment.setItem(item);
            comment.setCreated(now);
            return commentMapper.toCommentDto(commentRepository.save(comment));
        } else throw new BadRequestException(String.format("Не найден пользователь %d с предметом %d", userId, itemId));
    }


    private boolean checkEmptyMessage(String message) {
        return message == null || message.trim().isEmpty();
    }

    private Item itemOwnerValidation(long id, long ownerId) {
        userService.findUserById(ownerId);
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + id + " не найден."));
        if (item.getOwnerId() == ownerId) {
            return item;
        }
        throw new NotFoundException("Предмет с id " + id + " и владельцем " + ownerId + " не найден.");
    }

    private void updateBookings(ItemDto itemDto) {
        List<Booking> bookings = bookingRepository.findAllByItemIdOrderByEndDesc(itemDto.getId());
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = bookings.stream()
                .filter(booking -> !(booking.getStatus().equals(BookingStatus.REJECTED)))
                .filter(booking -> booking.getStart().isBefore(now))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);
        Booking nextBooking = bookings.stream()
                .filter(booking -> !(booking.getStatus().equals(BookingStatus.REJECTED)))
                .filter(booking -> booking.getStart().isAfter(now))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
        if (lastBooking != null) {
            itemDto.setLastBooking(bookingMapper.toItemBookingDto(lastBooking));
        }
        if (nextBooking != null) {
            itemDto.setNextBooking(bookingMapper.toItemBookingDto(nextBooking));
        }
    }

}
