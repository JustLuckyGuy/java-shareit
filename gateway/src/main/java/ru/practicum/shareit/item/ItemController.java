package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

@Controller
@RequestMapping("/items")
@Slf4j
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<ItemDto> postItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                            @RequestBody @Validated ItemDto itemDto) {
        log.info("Post item {}, userId={}", itemDto, userId);
        return client.postItem(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> updateItem(@RequestHeader("X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId,
                                              @RequestBody @Validated ItemDto itemDto) {
        log.info("Update item {}, userId={}, itemId={}", itemDto, userId, itemId);
        return client.updateItem(userId, itemId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> getItem(@PathVariable long itemId) {
        log.info("Get item with id {}", itemId);
        return client.getItem(itemId);
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getUserItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("Get items of user with id {}", userId);
        return client.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> itemSearch(@RequestParam String text) {
        log.info("Search item with text {}", text);
        return client.itemSearch(text);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<ItemDto> deleteItem(@RequestHeader("X-Sharer-User-Id") long userId, @PathVariable long itemId) {
        log.info("Delete user's item with id {}, userId={}", itemId, userId);
        return client.deleteItem(userId, itemId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<CommentDTO> addComment(@RequestHeader("X-Sharer-User-Id") long userId,
                                                 @PathVariable long itemId,
                                                 @RequestBody @Valid CommentDTO commentDto) {
        log.info("Post comment {}, itemId={}, userId={}", commentDto, itemId, userId);
        return client.addComment(userId, itemId, commentDto);
    }
}
