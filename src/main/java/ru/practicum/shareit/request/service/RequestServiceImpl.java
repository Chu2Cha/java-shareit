package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserService userService;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final UserMapper userMapper;

    @Override
    public ItemRequestDto create(ItemRequestDto requestDto, long requesterId) {
        User requester = userRepository.findById(requesterId).orElseThrow(
                () -> new NotFoundException("Пользователь с id " + requesterId + " не найден."));
        ItemRequest itemRequest = requestMapper.toRequest(requestDto, requester);
        return requestMapper.toRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto findById(long id, long userId) {
        userService.findUserById(userId);
        ItemRequest itemRequest = requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запрос не найден."));
        return getDtoFromRequest(itemRequest);
    }

    @Override
    public List<ItemRequestDto> findAllFromUser(long requesterId) {
        userService.findUserById(requesterId);
        List<ItemRequest> itemRequestList = requestRepository.findAllByRequesterIdOrderByCreatedDesc(requesterId);
        return getItemRequestDtoList(itemRequestList);
    }

    @Override
    public List<ItemRequestDto> findAllRequestsForAnswer(long userId, int from, int size) {
        userService.findUserById(userId);
        Pageable page = PageRequest.of(from / size, size, Sort.by("created"));
        List<ItemRequest> itemRequestList = requestRepository.findAllByRequesterIdIsNot(userId, page);
        return getItemRequestDtoList(itemRequestList);
    }

    private List<ItemRequestDto> getItemRequestDtoList(List<ItemRequest> itemRequestList) {
        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequestList) {
            ItemRequestDto itemRequestDto = getDtoFromRequest(itemRequest);
            itemRequestDtoList.add(itemRequestDto);
        }
        return itemRequestDtoList;
    }

    private ItemRequestDto getDtoFromRequest(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = requestMapper.toRequestDto(itemRequest);
        itemRequestDto.setRequester(userMapper.toUserDto(itemRequest.getRequester()));
        List<ItemDto> itemDtoList = itemRepository.findByRequestId(itemRequest.getId()).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        itemRequestDto.setItems(itemDtoList);
        return itemRequestDto;
    }
}
