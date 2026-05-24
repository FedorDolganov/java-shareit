package ru.practicum.shareit.mappings;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.AddItemDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.user.User;

import java.util.List;

public class ItemMapping {

    public static ItemDto from(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable()
                //, item.getRequest()
        );
    }

    public static Item to(ItemDto item, User user) {
        return new Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                user
                //, 0L
        );
    }

    public static Item to(AddItemDto item, User user) {
        return new Item(
                0L,
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                user
                //, 0L
        );
    }

    public static ItemDtoWithBooking fromWithBooking(Item item, Booking nextBooking, Booking lastBooking, List<CommentDto> comments) {
        return new ItemDtoWithBooking(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                // item.getRequest(),
                comments,
                BookingMapping.from(nextBooking),
                BookingMapping.from(lastBooking)
        );
    }

}
