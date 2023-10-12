package ru.practicum.shareit.item.comment.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    ItemRepository itemRepository;

    private Item item1;

    @BeforeEach
    public void setUp() {
        item1 = new Item();
        item1.setName("Item name");
        item1.setDescription("Good item");
        item1.setAvailable(true);
        item1.setOwnerId(1L);
        itemRepository.save(item1);

        Comment comment1 = new Comment();
        comment1.setText("First comment text");
        comment1.setItem(item1);
        comment1.setCreated(LocalDateTime.of(2023, 10, 1, 1, 11));
        commentRepository.save(comment1);

        Comment comment2 = new Comment();
        comment2.setText("Second comment text");
        comment2.setItem(item1);
        comment2.setCreated(LocalDateTime.of(2023, 10, 2, 2, 22));
        commentRepository.save(comment2);
    }

    @Test
    void findAllByItemId() {
        List<Comment> comments = commentRepository.findAllByItemId(item1.getId());
        assertEquals(2, comments.size());
    }

    @AfterEach
    public void cleanBase() {
        itemRepository.deleteAll();
        commentRepository.deleteAll();
    }
}