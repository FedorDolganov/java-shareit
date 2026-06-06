package ru.practicum.shareit.mappings;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;

@UtilityClass
public class ItemMapping {

    public static ItemDto from(Item item) {
        if (item.getRequest() == null) {
            return new ItemDto(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    null
            );
        } else {
            return new ItemDto(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    item.getRequest().getId()
            );
        }
    }

    public static Item to(ItemDto item, User user, ItemRequest itemRequest) {
        return new Item(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getAvailable(),
                user,
                itemRequest
        );
    }

    public static ItemDtoWithBooking fromWithBooking(Item item, Booking nextBooking, Booking lastBooking, List<CommentDto> comments) {
        if (item.getRequest() == null) {
            return new ItemDtoWithBooking(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    null,
                    comments,
                    BookingMapping.from(nextBooking),
                    BookingMapping.from(lastBooking)
            );
        } else {
            return new ItemDtoWithBooking(
                    item.getId(),
                    item.getName(),
                    item.getDescription(),
                    item.getAvailable(),
                    item.getRequest().getId(),
                    comments,
                    BookingMapping.from(nextBooking),
                    BookingMapping.from(lastBooking)
            );
        }
    }

}
