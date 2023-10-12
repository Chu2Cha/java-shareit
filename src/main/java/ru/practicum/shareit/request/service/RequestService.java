package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto create(ItemRequestDto requestDto, long requestorId);

    ItemRequestDto findById(long id, long userId);

    List<ItemRequestDto> findAllFromUser(long requestorId);

    List<ItemRequestDto> findAllRequestsForAnswer(long userId, int from, int size);
}
