package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.EmailAlreadyExistsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {
    private final Map<Integer, User> users;
    private final UserMapper userMapper;
    private Integer currentId;

    public UserServiceImpl(UserMapper userMapper) {
        this.userMapper = userMapper;
        users = new HashMap<>();
        currentId = 1;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        checkEmailInStorage(userDto);
        userDto.setId(currentId++);
        User user = userMapper.toUser(userDto);
        users.put(user.getId(), user);
        return userDto;
    }

    @Override
    public void removeUser(int id) {
        findUserById(id);
        users.remove(id);
    }

    @Override
    public UserDto updateUser(UserDto userDto, int id) {
        User userFromStorage = findUserById(id);
        userDto.setId(id);
        if (userDto.getName() == null) {
            checkEmailInStorage(userDto);
            userDto.setName(userFromStorage.getName());
        }
        if (userDto.getEmail() == null) {
            userDto.setEmail(userFromStorage.getEmail());
        }
        User user = userMapper.toUser(userDto);
        users.put(user.getId(), user);
        return userDto;
    }

    @Override
    public UserDto getUser(int id) {
        User user = findUserById(id);
        return userMapper.toUserDto(user);
    }


    private User findUserById(int id) {
        return Optional.ofNullable(users.get(id))
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден."));
    }

    private void checkEmailInStorage(UserDto userDto) {
        boolean emailExists = users.values().stream()
                .filter(u -> u.getId() != userDto.getId())
                .anyMatch(u -> u.getEmail().equals(userDto.getEmail()));
        if (emailExists) {
            throw new EmailAlreadyExistsException("Электронная почта уже занята!");
        }
    }
}
