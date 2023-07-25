package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper;
    private final UserService userService;
    private final Map<Integer, Item> items;
    private Integer currentId;


    public ItemServiceImpl(ItemMapper itemMapper, UserService userService) {
        this.itemMapper = itemMapper;
        this.userService = userService;
        items = new HashMap<>();
        currentId = 1;
    }

    @Override
    public List<ItemDto> getAllItems() {
        return null;
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, int ownerId) {
        userService.getUser(ownerId);
        itemDto.setId(currentId++);
        Item item = itemMapper.toItem(itemDto, ownerId);
        items.put(item.getId(), item);
        return itemDto;
    }


    @Override
    public ItemDto updateItem(ItemDto itemDto, int id) {
        return null;
    }

    @Override
    public ItemDto getItem(int id, int ownerId) {
        Item item = itemOwnerValidation(id, ownerId);
        return item != null ? itemMapper.toItemDto(item) : null;
    }

    @Override
    public void deleteItem(int id, int ownerId) {


    }

    private Item itemOwnerValidation(int id, int ownerId) {
        userService.getUser(ownerId);
        Item item = findItemById(id);
        if (item.getOwner() == ownerId) {
            return item;
        }
        return null;
    }

    private Item findItemById(int id) {
        return Optional.ofNullable(items.get(id))
                .orElseThrow(() -> new NotFoundException("Предмет с id " + id + " не найден."));
    }
}
