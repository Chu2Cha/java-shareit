package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private Map<Integer, User> users;
    private int id;

    public UserRepositoryImpl() {
        users = new HashMap<>();
        id = 1;
    }

    @Override
    public User saveUser(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUser(int id) {
        return users.get(id);
    }

    @Override
    public void removeUser(int id) {
        users.remove(id);
    }

    @Override
    public boolean checkEmail(int userId, String email) {
        return users.values().stream()
                .filter(u -> u.getId() != userId)
                .anyMatch(u -> u.getEmail().equals(email));
    }
}
