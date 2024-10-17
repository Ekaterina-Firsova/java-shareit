package ru.practicum.shareit.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class CommentMapper {
    public CommentDto mapToCommentDto(Comment comment, User user) {
        if (comment == null) {
            return null;
        }
        return CommentDto.builder()
                .id(comment.getId())
                .created(comment.getCreated())
                .itemId(comment.getItemId())
                .authorName(user.getName())
                .text(comment.getText())
                .build();
    }
}
