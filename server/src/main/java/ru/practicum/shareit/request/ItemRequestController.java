package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.SendedItemRequestDto;
import ru.practicum.shareit.request.services.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@AllArgsConstructor
public class ItemRequestController {

    private ItemRequestService itemRequestService;


    @PostMapping
    public ItemRequestDto add(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemRequestDto request) {
        return itemRequestService.add(userId, request);
    }

    @GetMapping
    public List<SendedItemRequestDto> getRequestsByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemRequestService.getByUser(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAll() {
        return itemRequestService.getAll();
    }

    @GetMapping("/{requestId}")
    public SendedItemRequestDto get(@PathVariable long requestId) {
        return itemRequestService.get(requestId);
    }

}
