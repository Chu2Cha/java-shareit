package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemMapper itemMapper = new ItemMapper();
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public List<ItemDto> getAllItemsFromUser(long ownerId) {

        return itemRepository.findAllByOwnerId(ownerId).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto createItem(ItemDto itemDto, long ownerId) {
        userService.findUserById(ownerId);
        Item item = itemMapper.toItem(itemDto);
        item.setOwnerId(ownerId);
        item.setRequest(null);
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
    public ItemDto findById(long id, long ownerId) {
        userService.findUserById(ownerId);  // выбрасывает ошибку, если неавторизованный пользователь зайдет.
        return itemMapper.toItemDto(itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Предмет с id " + id + " не найден.")));
    }

    @Override
    public void deleteItem(long id, long ownerId) {
        itemOwnerValidation(id, ownerId);
        itemRepository.deleteById(id);
    }

    @Override
    public List<ItemDto> searchItems(String message, long ownerId) {
        userService.findUserById(ownerId);
        if (checkEmptyMessage(message)) {
            return Collections.emptyList();
        }
        return itemRepository.findAll().stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toLowerCase().contains(message.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(message.toLowerCase())))
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
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

}
