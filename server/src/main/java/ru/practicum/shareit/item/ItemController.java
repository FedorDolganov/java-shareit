package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.services.ItemService;

import java.util.List;

@RestController
@RequestMapping("/items")
@AllArgsConstructor
public class ItemController {

    private ItemService itemService;

    @GetMapping
    public List<ItemDtoWithBooking> getByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItemsByUser(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoWithBooking get(@PathVariable long itemId) {
        return itemService.getItem(itemId);
    }

    @PostMapping()
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId, @RequestBody ItemDto item) {
        return itemService.addItem(item, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @RequestBody CommentDto comment) {
        return itemService.addComment(comment, itemId, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId, @RequestBody ItemDto item) {
        return itemService.updateItem(item, itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.searchItems(text);
    }

}
