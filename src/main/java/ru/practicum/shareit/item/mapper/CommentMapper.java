package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentDTO;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.Instant;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CommentMapper {
    public static Comment mapToComment(Item item, CommentDTO commentDto) {
        return Comment.builder()
                .item(item)
                .authorName(commentDto.getAuthorName())
                .text(commentDto.getText())
                .created(Instant.now())
                .build();
    }

    public static CommentDTO mapToDTO(Comment comment) {

        return CommentDTO.builder()
                .id(comment.getId())
                .authorName(comment.getAuthorName())
                .text(comment.getText())
                .created(comment.getCreated().toString())
                .build();
    }
}
