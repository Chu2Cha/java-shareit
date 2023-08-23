package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllItemsFromUser(long ownerId);

    ItemDto createItem(ItemDto itemDto, long ownerId);

    ItemDto updateItem(ItemDto itemDto, long id, long ownerId);

    ItemDto findById(long id, long ownerId);

    void deleteItem(long id, long ownerId);

    List<ItemDto> searchItems(String message, long ownerId);
}
