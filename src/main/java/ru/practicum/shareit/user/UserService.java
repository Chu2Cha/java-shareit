package ru.practicum.shareit.user;

import java.util.Map;

public interface UserService {
    Map<Integer, User> getAllUsers();
    User saveUser(User user);
}

