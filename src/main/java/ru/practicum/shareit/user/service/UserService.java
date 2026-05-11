package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.AddUserDto;
import ru.practicum.shareit.user.dto.UserDto;

public interface UserService {

    UserDto addUser(AddUserDto user);

    UserDto updateUser(UserDto user, long id);

    UserDto getUser(long id);

    void deleteUser(long id);

}
