package ru.practicum.shareit.item;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@Validated
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@RequestBody ItemDto itemDto,
                              @RequestHeader(Constants.USER_ID) long ownerId) {
        return itemService.createItem(itemDto, ownerId);
    }

    @DeleteMapping("/{id}")
    public void deleteItem(@PathVariable("id") long id, @RequestHeader(Constants.USER_ID) long ownerId) {
        itemService.deleteItem(id, ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable("id") long id, @RequestHeader(Constants.USER_ID) long ownerId) {
        return itemService.findById(id, ownerId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsFromUser(@RequestHeader(Constants.USER_ID) long ownerId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        return itemService.getAllItemsFromUser(ownerId, from, size);
    }

    @PatchMapping("/{id}")
    public ItemDto updateItem(@RequestBody ItemDto itemDto,
                              @PathVariable("id") long id,
                              @RequestHeader(Constants.USER_ID) long ownerId) {
        return itemService.updateItem(itemDto, id, ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam("text") String message,
                                     @RequestHeader(Constants.USER_ID) long ownerId,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) {
        return itemService.searchItems(message, ownerId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable("itemId") long itemId,
                                    @RequestHeader(Constants.USER_ID) long userId,
                                    @RequestBody CommentDto commentDto) {
        return itemService.addComment(itemId, userId, commentDto);
    }

}
