package ru.practicum.shareit.comment.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperTest {
    @Test
    public void testMapToCommentDto_CommentIsNull_ShouldReturnNull() {
        Comment comment = null;

        CommentDto result = CommentMapper.mapToCommentDto(comment, null);

        assertNull(result, "Expected result to be null when booking is null");
    }
}
