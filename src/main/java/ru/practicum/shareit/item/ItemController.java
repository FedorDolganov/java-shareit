package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.AddItemDto;
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

    @GetMapping("/{id}")
    public ItemDtoWithBooking get(@PathVariable long id) {
        return itemService.getItem(id);
    }

    @PostMapping()
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") long userId, @Valid @RequestBody AddItemDto item) {
        return itemService.addItem(item, userId);
    }

    @PostMapping("/{id}/comment")
    public CommentDto addComment(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id, @Valid @RequestBody CommentDto comment) {
        return itemService.addComment(comment, id, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long id, @RequestBody ItemDto item) {
        return itemService.updateItem(item, id, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.searchItems(text);
    }

}
