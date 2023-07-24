package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;


import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {
    private final Map<Integer,User> users = new HashMap<>();
    private Integer id = 1;

    @Override
    public Map<Integer, User> getAllUsers() {
        return users;
    }

    @Override
    public User saveUser(User user) {
        if(checkEmailInStorage(user))
            throw new RuntimeException();
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    private boolean checkEmailInStorage(User user) {
        for(User checkEmailUser: users.values()){
            if(checkEmailUser.getEmail().equals(user.getEmail())){
                return true;
            }
        }
        return false;
    }
}
