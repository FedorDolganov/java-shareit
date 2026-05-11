package ru.practicum.shareit.item.repositories;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.AddItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemRepository {

    ItemDto getItem(long id);

    List<ItemDto> getItemsByUser(long userId);

    List<ItemDto> searchItems(String text);

    ItemDto addItem(AddItemDto item, long userId);

    ItemDto updateItem(ItemDto item, long id, long userId);

}
