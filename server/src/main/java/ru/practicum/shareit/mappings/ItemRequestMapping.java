package ru.practicum.shareit.mappings;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.SendedItemRequestDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@UtilityClass
public class ItemRequestMapping {

    public static ItemRequest from(ItemRequestDto requestDto, User user) {
        return new ItemRequest(
                requestDto.getId(),
                requestDto.getDescription(),
                user,
                requestDto.getCreated()
        );
    }

    public static ItemRequestDto to(ItemRequest request) {
        return new ItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequestor().getId(),
                request.getCreated()
        );
    }

    public static SendedItemRequestDto toSended(ItemRequest request, List<ItemDto> items) {
        return new SendedItemRequestDto(
                request.getId(),
                request.getDescription(),
                request.getRequestor().getId(),
                request.getCreated(),
                items
        );
    }

}
