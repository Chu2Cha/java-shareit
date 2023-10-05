package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class RequestRepositoryTest {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    private final Pageable page = PageRequest.of(0 / 10, 10);

    private User user1;
    private User user2;

    @BeforeEach
    public void setUp() {
        user1 = new User();
        user1.setName("Name1");
        user1.setEmail("email1@mail.com");
        userRepository.save(user1);

        user2 = new User();
        user2.setName("Name2");
        user2.setEmail("email2@mail.com");
        userRepository.save(user2);

        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setDescription("FirstRequest");
        itemRequest1.setRequestor(user1);
        itemRequest1.setCreated(LocalDateTime.of(2023, 10, 2, 11, 22));
        requestRepository.save(itemRequest1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setDescription("SecondRequest");
        itemRequest2.setRequestor(user2);
        itemRequest2.setCreated(LocalDateTime.of(2023, 10, 4, 12, 33));
        requestRepository.save(itemRequest2);
    }

    @Test
    void findAllByRequestorIdOrderByCreatedDesc() {
        List<ItemRequest> requests = requestRepository.findAllByRequestorIdOrderByCreatedDesc(user1.getId());
        assertEquals(1, requests.size());
        assertEquals("Name1", requests.get(0).getRequestor().getName());
    }

    @Test
    void findAllByRequestorIdIsNot() {
        List<ItemRequest> requests = requestRepository.findAllByRequestorIdIsNot(user2.getId(), page);
        assertEquals(1, requests.size());
        assertEquals("Name1", requests.get(0).getRequestor().getName());
    }

    @AfterEach
    public void cleanBase() {
        requestRepository.deleteAll();
        userRepository.deleteAll();
    }
}