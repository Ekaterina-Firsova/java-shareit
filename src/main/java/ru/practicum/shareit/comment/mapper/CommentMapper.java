package ru.practicum.shareit.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;

@UtilityClass
public class CommentMapper {
    public CommentDto mapToCommentDto(Comment comment) {
        if (comment == null) {
            return null;
        }
        return CommentDto.builder()
                .id(comment.getId())
                .created(comment.getCreated())
                .item(comment.getItem())
                .author(comment.getAuthor())
                .text(comment.getText())
                .build();
    }

    public static Comment mapToComment(CommentDto commentDto) {
        if (commentDto == null) {
            return null;
        }

        return Comment.builder()
                .id(commentDto.getId())
                .created(commentDto.getCreated())
                .item(commentDto.getItem())
                .author(commentDto.getAuthor())
                .text(commentDto.getText())
                .build();
    }
}
