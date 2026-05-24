package ru.practicum.shareit.mappings;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.AddUserDto;
import ru.practicum.shareit.user.dto.UserDto;

public class UserMapping {

    public static UserDto from(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User to(UserDto user) {
        return new User(
                user.getId(),
                user.getName(),
                user.getEmail()
        );
    }

    public static User to(AddUserDto user) {
        return new User(
                0L,
                user.getName(),
                user.getEmail()
        );
    }

}
