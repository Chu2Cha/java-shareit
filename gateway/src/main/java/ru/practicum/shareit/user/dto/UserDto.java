package ru.practicum.shareit.user.dto;

import lombok.*;
import ru.practicum.shareit.validation.CreateValidation;
import ru.practicum.shareit.validation.UpdateValidation;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long id;
    @NotBlank (groups = CreateValidation.class)
    private String name;
    @NotBlank (groups = CreateValidation.class)
    @Email (groups = {CreateValidation.class, UpdateValidation.class})
    private String email;
}
