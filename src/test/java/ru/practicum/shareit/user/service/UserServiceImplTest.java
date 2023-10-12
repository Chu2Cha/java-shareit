package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Captor
    private ArgumentCaptor<User> argumentCaptor;

    User user;

    @BeforeEach
    void beforeEach() {
        user = new User(1, "Name", "email@mail.ru");
    }

    @Test
    void findUserById_whenUserFound_thenReturnUserDto() {
        User expectedUser = user;
        long userId = user.getId();
        UserDto expectedUserDto = new UserDto(expectedUser.getId(),
                expectedUser.getName(), expectedUser.getEmail());
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));

        UserDto actualUserDto = userService.findUserById(userId);

        assertEquals(expectedUserDto, actualUserDto);
        verify(userRepository).findById(userId);
    }

    @Test
    void findUserById_whenUserNotFound_thenReturnNotFoundException() {
        long userId = 100L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.findUserById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void createUser() {
        UserDto expectedUserDto = new UserDto(user.getId(), user.getName(),
                user.getEmail());
        when(userRepository.save(user)).thenReturn(user);
        UserDto actualUserDto = userService.saveUser(expectedUserDto);
        assertEquals(expectedUserDto, actualUserDto);
        verify(userRepository).save(user);
    }

    @Test
    void removeUser_whenUserIdFound_shouldDeleteUser() {
        long userId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.removeUser(userId);
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void removeUser_whenUserIdNotFound_shouldFailWithoutDeleteId() {
        long userFakeId = 100L;
        doThrow(NotFoundException.class)
                .when(userRepository).findById(userFakeId);
        assertThrows(NotFoundException.class,
                () -> userService.removeUser(userFakeId));
        verify(userRepository, never()).deleteById(userFakeId);
    }

    @Test
    void updateUser_whenUserNotFound_shouldFailWithoutUpdate() {
        long userId = 100L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> userService.findUserById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void updateUser_whenUserFoundAndNewName_shouldUpdate() {
        long userId = 1L;
        String newName = "Jane Doe";
        UserDto userDto = new UserDto(userId, newName, null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
            User savedUser = invocation.getArgument(0);
            savedUser.setName(newName);
            return savedUser;
        });

        UserDto updatedUser = userService.updateUser(userDto, userId);

        assertEquals(newName, updatedUser.getName());
        assertEquals(user.getEmail(), updatedUser.getEmail());
    }

    @Test
    void updateUser_whenUserFoundAndNewEmail_shouldUpdate() {
        long userId = 1L;
        String newMail = "new@mail.com";
        UserDto userDto = new UserDto(userId, null, newMail);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> {
                    User savedUser = invocation.getArgument(0);
                    savedUser.setEmail(newMail);
                    return savedUser;
                });

        UserDto updatedUser = userService.updateUser(userDto, userId);

        assertEquals(newMail, updatedUser.getEmail());
        assertEquals(user.getName(), updatedUser.getName());
    }

    @Test
    void getAllUsers_whenUsersInBase_shouldReturnListOfUsers() {
        User user2 = new User(2L, "SecondName", "second@name.ru");
        when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));

        List<UserDto> userDtoList = userService.getAllUsers();

        assertEquals(2, userDtoList.size());
        assertEquals("Name", userDtoList.get(0).getName());
        assertEquals("SecondName", userDtoList.get(1).getName());

    }
}