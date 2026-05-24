package ru.practicum.shareit.user.repositories;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.mappings.UserMapping;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.AddUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.List;

@Component
public class UserRepositoryImpl implements UserRepository {

    private HashMap<Long, User> allUsers;
    private long lastUserId;

    public UserRepositoryImpl() {
        this.allUsers = new HashMap<>();
        this.lastUserId = 0;
    }


    public List<User> getAllUsers() {
        return allUsers.values().stream().toList();
    }

    @Override
    public UserDto getUser(long id) {
        return UserMapping.toUserDto(allUsers.get(id));
    }

    @Override
    public UserDto addUser(AddUserDto user) {
        User finalUser = new User(
                lastUserId,
                user.getName(),
                user.getEmail()
        );

        allUsers.put(lastUserId, finalUser);

        lastUserId++;

        return UserMapping.toUserDto(finalUser);
    }

    @Override
    public UserDto updateUser(UserDto user, long id) {
        User finalUser = allUsers.get(id);

        if (user.getName() != null) {
            finalUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            finalUser.setEmail(user.getEmail());
        }

        return UserMapping.toUserDto(finalUser);
    }

    @Override
    public void deleteUser(long id) {
        allUsers.remove(id);
    }

    @Override
    public boolean containsUser(long userId) {
        return allUsers.containsKey(userId);
    }

}
