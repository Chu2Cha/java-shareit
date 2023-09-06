package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;


public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderByIdAsc(Long ownerId, Pageable page);

    List<Item> findByRequestId(long requestId);

    @Query("SELECT i FROM Item i "
            + "WHERE upper(i.name) like upper(concat('%', ?1, '%')) "
            + "OR upper(i.description) like upper(concat('%', ?1, '%')) "
            + "AND i.available = true")
    List<Item> searchItems(String text, Pageable page);

}
