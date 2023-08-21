//package ru.practicum.shareit.item.service;
//
//import org.springframework.stereotype.Service;
//import ru.practicum.shareit.exceptions.NotFoundException;
//import ru.practicum.shareit.item.dto.ItemDto;
//import ru.practicum.shareit.item.dto.ItemMapper;
//import ru.practicum.shareit.item.model.Item;
//import ru.practicum.shareit.item.repository.ItemRepository;
//import ru.practicum.shareit.user.service.UserService;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class ItemServiceImpl implements ItemService {
//    private final ItemMapper itemMapper = new ItemMapper();
//    private final UserService userService;
//    private final ItemRepository itemRepository;
//
//
//    public ItemServiceImpl(UserService userService, ItemRepository itemRepository) {
//        this.userService = userService;
//        this.itemRepository = itemRepository;
//    }
//
//    @Override
//    public List<ItemDto> getAllItemsFromUser(int ownerId) {
//        return itemRepository.getAllItemsFromUser(ownerId).stream()
//                .map(itemMapper::toItemDto)
//                .collect(Collectors.toList());
//    }
//
//    @Override
//    public ItemDto createItem(ItemDto itemDto, int ownerId) {
//        userService.getUser(ownerId);
//        return itemMapper.toItemDto(itemRepository.createItem(itemMapper.toItem(itemDto, ownerId)));
//    }
//
//    @Override
//    public ItemDto updateItem(ItemDto itemDto, int id, int ownerID) {
//        Item itemFromStorage = itemOwnerValidation(id, ownerID);
//        itemDto.setId(id);
//        if (itemDto.getName() == null) {
//            itemDto.setName(itemFromStorage.getName());
//        }
//        if (itemDto.getDescription() == null) {
//            itemDto.setDescription(itemFromStorage.getDescription());
//        }
//        if (itemDto.getAvailable() == null) {
//            itemDto.setAvailable(itemFromStorage.getAvailable());
//        }
//        Item item = itemMapper.toItem(itemDto, ownerID);
//        return itemMapper.toItemDto(itemRepository.updateItem(item));
//
//    }
//
//    @Override
//    public ItemDto getItem(int id, int ownerId) {
//        userService.getUser(ownerId);  // выбрасывает ошибку, если неавторизованный пользователь зайдет.
//        return itemMapper.toItemDto(itemRepository.getItem(id));
//    }
//
//    @Override
//    public void deleteItem(int id, int ownerId) {
//        itemOwnerValidation(id, ownerId);
//        itemRepository.removeItem(id);
//    }
//
//    @Override
//    public List<ItemDto> searchItems(String message, int ownerId) {
//        userService.getUser(ownerId);
//        if (checkEmptyMessage(message)) {
//            return Collections.emptyList();
//        }
//        return itemRepository.searchItems(message, ownerId)
//                .stream()
//                .map(itemMapper::toItemDto)
//                .collect(Collectors.toList());
//    }
//
//    private boolean checkEmptyMessage(String message) {
//        return message == null || message.trim().isEmpty();
//    }
//
//    private Item itemOwnerValidation(int id, int ownerId) {
//        userService.getUser(ownerId);
//        Item item = findItemById(id);
//        if (item.getOwner() == ownerId) {
//            return item;
//        }
//        throw new NotFoundException("Предмет с id " + id + " и владельцем " + ownerId + " не найден.");
//    }
//
//    private Item findItemById(int id) {
//        return Optional.ofNullable(itemRepository.getItem(id))
//                .orElseThrow(() -> new NotFoundException("Предмет с id " + id + " не найден."));
//    }
//
//}
