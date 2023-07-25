package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Validated
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public UserDto saveNewUser(@Valid @RequestBody UserDto userDto) {
        return userService.saveUser(userDto);
    }
    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id) {
        userService.removeUser(id);
    }
    @PatchMapping("/{id}")
    public UserDto update(@RequestBody UserDto userDto, @PathVariable int id) {
        return userService.updateUser(userDto,id);
    }
    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable("id") int id) {
        return userService.getUser(id);
    }
}
