package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private  UserRepository userRepository;

    @Mock
    private  UserMapper userMapper;
    @Test
    void findUserById_whenUserFound_thenReturnUserDto() {
        long userId = 1L;
        User expectedUser = new User(userId, "Name", "email@mail.ru");
        UserDto expectedUserDto = new UserDto(expectedUser.getId(),
                expectedUser.getName(), expectedUser.getEmail());
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

       UserDto actualUserDto = userService.findUserById(userId);

       assertEquals(expectedUserDto, actualUserDto);
       verify(userRepository).findById(userId);
    }
    @Test
    void findUserById_whenUserNotFound_thenReturnNotFoundExceprion() {
        long userId = 100L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findUserById(userId));
        verify(userRepository).findById(userId);
    }
}