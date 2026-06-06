package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.HashMap;
import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }


    public ResponseEntity<Object> getItemsByUser(long userId) {
        return get("", userId);
    }

    public ResponseEntity<Object> getItem(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> addItem(long userId, @Valid ItemDto item) {
        return post("", userId, item);
    }

    public ResponseEntity<Object> addComment(long userId, @Valid CommentDto comment, long itemId) {
        return post("/" + itemId + "/comment", userId, comment);
    }

    public ResponseEntity<Object> updateItem(long userId, ItemDto item, long itemId) {
        return patch("/" + itemId, userId, item);
    }

    public ResponseEntity<Object> searchItems(long userId, String text) {
        Map<String, Object> map = new HashMap<>();

        map.put("text", text);

        return get("", userId, map);
    }
}