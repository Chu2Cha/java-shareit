package ru.practicum.shareit.item.comment.dto;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.comment.model.Comment;

@Component
@AllArgsConstructor
public class CommentMapper {

    public Comment toComment(CommentDto commentDto) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .created(commentDto.getCreated())
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
