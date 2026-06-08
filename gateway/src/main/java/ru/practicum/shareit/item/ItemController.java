package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;


@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getByUser(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get items by user userId={}", userId);
        return itemClient.getItemsByUser(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @PathVariable long itemId) {
        log.info("Get item {}, userId={}", itemId, userId);
        return itemClient.getItem(userId, itemId);
    }

    @PostMapping()
    public ResponseEntity<Object> add(@RequestHeader("X-Sharer-User-Id") long userId,
                                      @Valid @RequestBody ItemDto item) {
        log.info("Create item {}, userId={}", item, userId);
        return itemClient.addItem(userId, item);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                             @PathVariable long itemId,
                                             @Valid @RequestBody CommentDto comment) {
        log.info("Create comment {}, itemId={}, userId={}", comment, itemId, userId);
        return itemClient.addComment(userId, comment, itemId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @PathVariable long itemId,
                                         @RequestBody ItemDto item) {
        log.info("Update item {}, userId={}, item={}", itemId, userId, item);
        return itemClient.updateItem(userId, item, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader("X-Sharer-User-Id") long userId,
                                         @RequestParam String text) {
        log.info("Search items userId={}, text={}", userId, text);
        return itemClient.searchItems(userId, text);
    }

}