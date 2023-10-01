package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;


    @Test
    void findAllByOwnerIdOrderByIdAsc() {
    }

    @Test
    void findByRequestId() {
    }

    @Test
    void searchItems() {
    }
}