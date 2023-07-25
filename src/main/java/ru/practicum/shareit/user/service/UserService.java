package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    List<UserDto> getAllUsers();
    UserDto saveUser(UserDto userDto);

    void removeUser(int id);

    UserDto updateUser(UserDto userDto, int id);

    UserDto getUser(int id);
}

