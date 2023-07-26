package ru.practicum.shareit.item.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

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
    public List<ItemDto> getAllItemsFromUser(int ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == ownerId)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
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
    public ItemDto updateItem(ItemDto itemDto, int id, int ownerID) {
        Item itemFromStorage = itemOwnerValidation(id, ownerID);
        itemDto.setId(itemFromStorage.getId());
        if (itemDto.getName() == null) {
            itemDto.setName(itemFromStorage.getName());
        }
        if (itemDto.getDescription() == null) {
            itemDto.setDescription(itemFromStorage.getDescription());
        }
        if (itemDto.getAvailable() == null) {
            itemDto.setAvailable(itemFromStorage.getAvailable());
        }
        Item item = itemMapper.toItem(itemDto, ownerID);
        items.put(item.getId(), item);
        return itemDto;
    }

    @Override
    public ItemDto getItem(int id, int ownerId) {
        userService.getUser(ownerId);  // выбрасывает ошибку, если неавторизованный пользователь зайдет.
        return itemMapper.toItemDto(items.get(id));
    }

    @Override
    public void deleteItem(int id, int ownerId) {
        Item item = itemOwnerValidation(id, ownerId);
        items.remove(item.getId());
    }

    @Override
    public List<ItemDto> searchItem(String message, int ownerId) {
        userService.getUser(ownerId);
        if (message == null || message.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toLowerCase().contains(message.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(message.toLowerCase())))
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private Item itemOwnerValidation(int id, int ownerId) {
        userService.getUser(ownerId);
        Item item = findItemById(id);
        if (item.getOwner() == ownerId) {
            return item;
        }
        throw new NotFoundException("Предмет с id " + id + " и владельцем " + ownerId + " не найден.");
    }

    private Item findItemById(int id) {
        return Optional.ofNullable(items.get(id))
                .orElseThrow(() -> new NotFoundException("Предмет с id " + id + " не найден."));
    }


}
