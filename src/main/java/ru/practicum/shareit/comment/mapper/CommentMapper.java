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
                //.item(comment.getItem())
                .itemId(comment.getItemId())
                //.author(comment.getAuthor())
                .authorName(user.getName())
                .text(comment.getText())
                .build();
    }

    public static Comment mapToComment(CommentDto commentDto, User user) {
        if (commentDto == null) {
            return null;
        }

        return Comment.builder()
                .id(commentDto.getId())
                .created(commentDto.getCreated())
                //.item(commentDto.getItem())
                .itemId(commentDto.getItemId())
//                .author(commentDto.getAuthor())
                .authorId(user.getId())
                .text(commentDto.getText())
                .build();
    }
}
