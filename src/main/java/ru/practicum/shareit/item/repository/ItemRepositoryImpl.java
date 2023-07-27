package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private Map<Integer, Item> items;
    private int id;

    public ItemRepositoryImpl() {
        items = new HashMap<>();
        id = 1;
    }

    @Override
    public Item createItem(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public List<Item> getAllItemsFromUser(int ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwner() == ownerId)
                .collect(Collectors.toList());
    }

    @Override
    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item getItem(int id) {
        return items.get(id);
    }

    @Override
    public void removeItem(int id) {
        items.remove(id);
    }

    @Override
    public List<Item> searchItems(String message, int ownerId) {
        return items.values().stream()
                .filter(item -> item.getAvailable() &&
                        (item.getName().toLowerCase().contains(message.toLowerCase()) ||
                                item.getDescription().toLowerCase().contains(message.toLowerCase())))
                .collect(Collectors.toList());
    }
}
