package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItems();

    ItemDto createItem(ItemDto itemDto, int ownerId);

    ItemDto updateItem(ItemDto itemDto, int id);

    ItemDto getItem(int id, int ownerId);

    void deleteItem(int id, int ownerId);
}
