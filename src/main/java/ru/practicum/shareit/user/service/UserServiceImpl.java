package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ConflictException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper = new UserMapper();

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.getAllUsers().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto saveUser(UserDto userDto) {
        checkEmailInStorage(userDto);
        return userMapper.toUserDto(userRepository.saveUser(userMapper.toUser(userDto)));
    }

    @Override
    public void removeUser(int id) {
        findUserById(id);
        userRepository.removeUser(id);
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
        userRepository.updateUser(user);
        return userMapper.toUserDto(userRepository.updateUser(userMapper.toUser(userDto)));
    }

    @Override
    public UserDto getUser(int id) {
        User user = findUserById(id);
        return userMapper.toUserDto(user);
    }


    private User findUserById(int id) {
        return Optional.ofNullable(userRepository.getUser(id))
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден."));
    }

    private void checkEmailInStorage(UserDto userDto) {
        if (userRepository.checkEmail(userDto.getId(), userDto.getEmail())) {
            throw new ConflictException("Электронная почта уже занята!");
        }
    }
}
