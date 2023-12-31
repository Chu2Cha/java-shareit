package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.constants.Constants;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.validation.CreateValidation;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Validated(CreateValidation.class)
                                             @RequestBody ItemDto itemDto,
                                             @RequestHeader(Constants.USER_ID) long ownerId) {
        log.info("Creating item {}", itemDto);
        return itemClient.createItem(ownerId, itemDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable("id") long id, @RequestHeader(Constants.USER_ID) long ownerId) {
        log.info("Delete item id =  {}", id);
        return itemClient.deleteItem(id, ownerId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable("id") long id, @RequestHeader(Constants.USER_ID) long ownerId) {
        return itemClient.getItem(id, ownerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsFromUser(@RequestHeader(Constants.USER_ID) long ownerId,
                                                      @RequestParam(defaultValue = "0") int from,
                                                      @RequestParam(defaultValue = "10") int size) {
        return itemClient.getAllItemsFromUser(ownerId, from, size);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                             @PathVariable("id") long id,
                                             @RequestHeader(Constants.USER_ID) long ownerId) {
        return itemClient.updateItem(itemDto, id, ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam("text") String message,
                                             @RequestHeader(Constants.USER_ID) long ownerId,
                                             @RequestParam(defaultValue = "0") int from,
                                             @RequestParam(defaultValue = "10") int size) {
        return itemClient.searchItems(message, ownerId, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@PathVariable("itemId") long itemId,
                                                @RequestHeader(Constants.USER_ID) long userId,
                                                @RequestBody CommentDto commentDto) {
        return itemClient.createComment(itemId, userId, commentDto);
    }
}
