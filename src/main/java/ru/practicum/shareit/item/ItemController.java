package ru.practicum.shareit.item;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
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
    public ItemDto createItem(@Valid @RequestBody ItemDto itemDto, @RequestHeader(OWNER_ID) int ownerId){
        return itemService.createItem(itemDto, ownerId);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id, @RequestHeader(OWNER_ID) int ownerId) {
        itemService.deleteItem(id, ownerId);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable("id") int id, @RequestHeader(OWNER_ID) int ownerId) {
        return itemService.getItem(id, ownerId);
    }

}
