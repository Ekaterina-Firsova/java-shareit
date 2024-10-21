package ru.practicum.shareit.comment.mapper;

import jakarta.validation.constraints.NotNull;
import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.user.model.User;

@UtilityClass
public class CommentMapper {
    public CommentDto mapToCommentDto(@NotNull Comment comment, User user) {
        return CommentDto.builder()
                .id(comment.getId())
                .created(comment.getCreated())
                .itemId(comment.getItemId())
                .authorName(user.getName())
                .text(comment.getText())
                .build();
    }
}
