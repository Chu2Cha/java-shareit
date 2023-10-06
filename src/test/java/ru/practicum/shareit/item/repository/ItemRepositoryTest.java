package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private RequestRepository requestRepository;

    Pageable page = PageRequest.of(0 / 10, 10);
    private ItemRequest itemRequest;

    @BeforeEach
    public void setUp() {
        itemRequest = new ItemRequest();
        itemRequest.setDescription("RequestDescription");
        requestRepository.save(itemRequest);

        Item item1 = new Item();
        item1.setName("ItemName");
        item1.setDescription("ItemDescription");
        item1.setAvailable(true);
        item1.setOwnerId(1L);
        item1.setRequest(itemRequest);
        itemRepository.save(item1);
    }

    @Test
    void findAllByOwnerIdOrderByIdAsc() {
        List<Item> items = itemRepository.findAllByOwnerIdOrderByIdAsc(1L, page);
        assertEquals(1, items.size());
    }

    @Test
    void findByRequestId() {
        List<Item> items = itemRepository.findByRequestId(itemRequest.getId());
        assertEquals(1, items.size());
    }

    @Test
    void searchItems() {
        List<Item> items = itemRepository.searchItems("item", page);
        assertEquals(1, items.size());
    }

    @AfterEach
    public void cleanBase() {
        requestRepository.deleteAll();
        itemRepository.deleteAll();
    }
}