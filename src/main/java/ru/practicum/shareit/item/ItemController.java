package ru.practicum.shareit.item;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private static final String OWNER_ID = "X-Sharer-User-Id";

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @PostMapping
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(OWNER_ID) long ownerId) {
        return itemService.createItem(itemDto, ownerId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") long id, @RequestHeader(OWNER_ID) long ownerId) {
        itemService.deleteItem(id, ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable("id") long id, @RequestHeader(OWNER_ID) long ownerId) {
        return itemService.findById(id, ownerId);
    }

    @GetMapping
    public List<ItemDto> getAllItemsFromUser(@RequestHeader(OWNER_ID) long ownerId,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                             @PositiveOrZero @RequestParam(defaultValue = "10") int size) {
        return itemService.getAllItemsFromUser(ownerId, from, size);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestBody ItemDto itemDto,
                          @PathVariable("id") long id,
                          @RequestHeader(OWNER_ID) long ownerId) {
        return itemService.updateItem(itemDto, id, ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItem(@RequestParam("text") String message,
                                    @RequestHeader(OWNER_ID) long ownerId,
                                    @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                    @PositiveOrZero @RequestParam(defaultValue = "10") int size) {
        return itemService.searchItems(message, ownerId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@PathVariable("itemId") long itemId,
                                    @RequestHeader(OWNER_ID) long userId,
                                    @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(itemId, userId, commentDto);

    }

}
