package ru.practicum.shareit.user;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@Validated
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public Map<Integer,User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping
    public User saveNewUser(@Valid @RequestBody User user) {
        return userService.saveUser(user);
    }
}
