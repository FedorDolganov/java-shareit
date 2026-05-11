package ru.practicum.shareit.item.repositories;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.NoPermutationsException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.AddItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.mappings.ItemMapping;
import ru.practicum.shareit.user.repositories.UserRepository;

import java.util.HashMap;
import java.util.List;

@Component
public class ItemRepositoryImpl implements ItemRepository {

    private HashMap<Long, Item> allItems;
    private long lastItemId;
    private UserRepository userRepository;

    public ItemRepositoryImpl(UserRepository repository) {
        this.allItems = new HashMap<>();
        this.lastItemId = 0;
        this.userRepository = repository;
    }

    @Override
    public ItemDto getItem(long id) {
        return ItemMapping.toItemDto(allItems.get(id));
    }

    @Override
    public List<ItemDto> getItemsByUser(long userId) {
        return allItems.values().stream()
                .filter(item -> item.getOwner() == userId)
                .map(ItemMapping::toItemDto)
                .toList();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        return allItems.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase()) || item.getDescription().toLowerCase().contains(text.toLowerCase())) && item.getAvailable())
                .map(ItemMapping::toItemDto)
                .toList();
    }

    @Override
    public ItemDto addItem(AddItemDto item, long userId) {
        if (!userRepository.containsUser(userId)) {
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

        return ItemMapping.toItemDto(finalItem);
    }

    @Override
    public ItemDto updateItem(ItemDto item, long id, long userId) {
        if (!userRepository.containsUser(userId)) {
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

        return ItemMapping.toItemDto(finalItem);
    }

}
