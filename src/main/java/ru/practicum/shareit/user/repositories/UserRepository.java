package ru.practicum.shareit.user.repositories;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.AddUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserRepository {

    List<User> getAllUsers();

    UserDto getUser(long id);

    UserDto addUser(AddUserDto user);

    UserDto updateUser(UserDto user, long id);

    void deleteUser(long id);

    boolean containsUser(long userId);

}
