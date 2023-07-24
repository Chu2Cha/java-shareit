package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.item.model.Item;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class ItemDto {
    private Integer id;
    @NonNull
    private String name;
    @NonNull
    private String description;
    @NonNull
    private boolean available;
    private String owner;
    @NonNull
    private Integer request;


    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
        );
    }

}
