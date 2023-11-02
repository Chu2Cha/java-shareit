package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validation.CreateValidation;
import ru.practicum.shareit.validation.UpdateValidation;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        log.info("Get all users");
        return userClient.getAllUsers();
    }

    @PostMapping
    public ResponseEntity<Object> saveNewUser(@Validated(CreateValidation.class)
                                              @RequestBody UserDto userDto) {
        log.info("Creating user {}", userDto);
        return userClient.saveNewUser(userDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable("id") long id) {
        log.info("Delete user id = {}", id);
        return userClient.delete(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@Validated(UpdateValidation.class)
                                         @RequestBody UserDto userDto,
                                         @PathVariable("id") long id) {
        log.info("Update id =  {}, user = {}", id, userDto);
        return userClient.update(userDto, id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable("id") long id) {
        log.info("Get user id = {}", id);
        return userClient.getUser(id);
    }

}
