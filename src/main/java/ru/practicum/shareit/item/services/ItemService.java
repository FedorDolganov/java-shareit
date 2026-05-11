package ru.practicum.shareit.item.services;

import ru.practicum.shareit.item.dto.AddItemDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto getItem(long id);

    List<ItemDto> getItemsByUser(long userId);

    List<ItemDto> searchItems(String text);

    ItemDto addItem(AddItemDto item, long userId);

    ItemDto updateItem(ItemDto item, long id, long userId);

}
