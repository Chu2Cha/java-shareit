package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.FutureOrPresent;

import java.time.LocalDateTime;
import java.util.List;


@Data
@Builder
@AllArgsConstructor
public class ItemRequestDto {
    private long id;

    @NotBlank
    private String description;

    private UserDto requester;

    @FutureOrPresent
    private LocalDateTime created;

    private List<ItemDto> items;
}

