package ru.practicum.shareit.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NoPermutationsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class ServiceRepositoryImpl implements ServiceRepository {

    private HashMap<Long, Item> allItems;
    private HashMap<Long, User> allUsers;

    private long lastItemId = 0;
    private long lastUserId = 0;

    public ServiceRepositoryImpl() {
        this.allItems = new HashMap<>();
        this.allUsers = new HashMap<>();
    }

    public List<User> getAllUsers() {
        return allUsers.values().stream().toList();
    }

    @Override
    public UserDto getUser(long id) {
        return User.toUserDto(allUsers.get(id));
    }

    @Override
    public UserDto addUser(User user) {
        User finalUser = new User(
                lastUserId,
                user.getName(),
                user.getEmail()
        );

        allUsers.put(lastUserId, finalUser);

        lastUserId++;

        return User.toUserDto(finalUser);
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

        return User.toUserDto(finalUser);
    }

    @Override
    public void deleteUser(long id) {
        allUsers.remove(id);
    }

    @Override
    public ItemDto getItem(long id) {
        return Item.toItemDto(allItems.get(id));
    }

    @Override
    public List<ItemDto> getItemsByUser(long userId) {
        return allItems.values().stream()
                .filter(item -> item.getOwner() == userId)
                .map(Item::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return allItems.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase())) && item.getAvailable())
                .map(Item::toItemDto)
                .toList();
    }

    @Override
    public ItemDto addItem(Item item, long userId) {
        if (!allUsers.containsKey(userId)) {
            throw new NotFoundException("Пользователь с таким индефикатором не найден");
        }

        Item finalItem = new Item(
                lastItemId,
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                userId,
                0L
        );

        allItems.put(lastItemId, finalItem);

        lastItemId++;

        return Item.toItemDto(finalItem);
    }

    @Override
    public ItemDto updateItem(ItemDto item, long id, long userId) {
        if (!allUsers.containsKey(userId)) {
            throw new NotFoundException("Пользователь с таким индефикатором не найден");
        }

        Item finalItem = allItems.get(id);

        if (finalItem.getOwner() != userId) {
            throw new NoPermutationsException("Пользователь не имеет права доступа на этот предмет");
        }

        if (item.getName() != null) {
            finalItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            finalItem.setDescription(item.getDescription());
        }

        finalItem.setAvailable(item.isAvailable());

        return Item.toItemDto(finalItem);
    }
}
