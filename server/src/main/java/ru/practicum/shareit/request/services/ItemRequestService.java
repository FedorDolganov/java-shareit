package ru.practicum.shareit.request.services;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.SendedItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto add(long userId, ItemRequestDto request);

    List<SendedItemRequestDto> getByUser(long userId);

    List<ItemRequestDto> getAll();

    SendedItemRequestDto get(long requestId);

}
