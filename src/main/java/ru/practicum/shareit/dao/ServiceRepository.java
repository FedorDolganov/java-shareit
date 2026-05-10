package ru.practicum.shareit.dao;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface ServiceRepository {

    List<User> getAllUsers();

    UserDto getUser(long id);

    UserDto addUser(User user);

    UserDto updateUser(UserDto user, long id);

    void deleteUser(long id);

    ItemDto getItem(long id);

    List<ItemDto> getItemsByUser(long userId);

    List<ItemDto> searchItems(String text);

    ItemDto addItem(Item item, long userId);

    ItemDto updateItem(ItemDto item, long id, long userId);

}
