package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    public User saveUser(User user);

    public List<User> getAllUsers();

    public User updateUser(User user);

    public User getUser(int id);

    public void removeUser(int id);

    boolean checkEmail(int userId, String email);
}
