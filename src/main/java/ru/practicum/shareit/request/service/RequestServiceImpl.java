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
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
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

    @Override
    public ItemRequestDto create(ItemRequestDto requestDto, long requesterId) {
        userService.findUserById(requesterId);
        User requester = userRepository.findById(requesterId).orElseThrow(
                () -> new NotFoundException("Пользователь с id " + requesterId + " не найден."));
        LocalDateTime now = LocalDateTime.now();
        requestDto.setCreated(now);
        ItemRequest itemRequest = requestMapper.toRequest(requestDto, requester);
        return requestMapper.toRequestDto(requestRepository.save(itemRequest));
    }

    @Override
    public ItemRequestDto findById(long id, long userId) {
        userService.findUserById(userId);
        ItemRequest itemRequest = requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Запрос не найден."));
        long requestId = itemRequest.getId();
        ItemRequestDto itemRequestDto = requestMapper.toRequestDto(itemRequest);
        itemRequestDto.setRequester(userService.findUserById(itemRequest.getRequester().getId()));
        List<ItemDto> itemDtoList = itemRepository.findByRequestId(requestId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        itemRequestDto.setItems(itemDtoList);
        return itemRequestDto;
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
            ItemRequestDto itemRequestDto = findById(itemRequest.getId(), itemRequest.getRequester().getId());
            itemRequestDtoList.add(itemRequestDto);
        }
        return itemRequestDtoList;
    }
}
