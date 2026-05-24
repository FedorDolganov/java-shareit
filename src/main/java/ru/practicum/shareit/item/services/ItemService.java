package ru.practicum.shareit.item.services;

import ru.practicum.shareit.item.dto.AddItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;

import java.util.List;

public interface ItemService {

    ItemDtoWithBooking getItem(long id);

    List<ItemDtoWithBooking> getItemsByUser(long userId);

    List<ItemDto> searchItems(String text);

    ItemDto addItem(AddItemDto item, long userId);

    ItemDto updateItem(ItemDto item, long id, long userId);

    CommentDto addComment(CommentDto comment, long item_id, long userId);
}
