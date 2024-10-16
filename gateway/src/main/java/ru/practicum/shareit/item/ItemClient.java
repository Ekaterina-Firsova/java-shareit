package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.comment.CommentDto;

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

    public ResponseEntity<Object> post(ItemDto itemDto, long userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> getAll(long userId) {
        return get("/", userId);
    }

    public ResponseEntity<Object> getById(long itemId, long userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> patch(long itemId, ItemDto itemDto) {
        return patch("/" + itemId, itemDto);
    }

    public ResponseEntity<Object> getText(String text) {
        return get("/search", null, Map.of("text", text));
    }

    public ResponseEntity<Object> createComment(Long itemId, CommentDto comment, Long userId) {
        return post("/" + itemId + "/comment", userId, comment);
    }
}