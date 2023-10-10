package ru.practicum.shareit.item.comment.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CommentMapperTest {

    @InjectMocks
    private CommentMapper commentMapper;

    private Comment comment;
    private CommentDto commentDto;
    private Item item;
    private User author;

    @BeforeEach
    void setUp() {
        author = new User();
        author.setId(1L);
        UserDto authorDto = new UserDto();
        authorDto.setId(author.getId());

        Item item = new Item();
        item.setId(1L);
        item.setOwnerId(2L);
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());

        comment = new Comment(1L, "Comment", item,
                author, LocalDateTime.now());
        commentDto = new CommentDto(1L, "Comment", author.getName(),
                comment.getCreated());
    }

    @Test
    void toComment() {
        Comment newComment = commentMapper.toComment(commentDto);
        assertEquals(newComment.getText(), commentDto.getText());
    }

    @Test
    void toCommentDto() {
        CommentDto newCommentDto = commentMapper.toCommentDto(comment);
        assertEquals(newCommentDto.getAuthorName(),
                comment.getAuthor().getName());
    }
}