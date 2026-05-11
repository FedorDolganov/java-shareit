package ru.practicum.shareit.item.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.AddItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repositories.ItemRepository;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private ItemRepository repository;

    @Override
    public ItemDto getItem(long id) {
        return repository.getItem(id);
    }

    @Override
    public List<ItemDto> getItemsByUser(long userId) {
        return repository.getItemsByUser(userId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }

        return repository.searchItems(text);
    }

    @Override
    public ItemDto addItem(AddItemDto item, long userId) {
        return repository.addItem(item, userId);
    }

    @Override
    public ItemDto updateItem(ItemDto item, long id, long userId) {
        return repository.updateItem(item, id, userId);
    }
}
