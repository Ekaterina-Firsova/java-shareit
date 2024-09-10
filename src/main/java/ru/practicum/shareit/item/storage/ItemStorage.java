package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.common.IdGenerator;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component
public class ItemStorage {
    private final Map<Long, Item> items = new HashMap<>();

    public Item create(Item item, User user) {
        item.setId(IdGenerator.getNextId(items));
        item.setOwner(user);
        items.put(item.getId(), item);
        return item;
    }

    public Collection<Item> findAll() {
        return items.values();
    }

    public Optional<Item> getById(Long itemId) {
        return Optional.ofNullable(items.get(itemId));
    }

    public Item update(Long id, Item newItem) {
        if (items.containsKey(id)) {
            Item oldItem = items.get(id);
            if (newItem.getName() != null) {
                oldItem.setName(newItem.getName());
            }
            if (newItem.getDescription() != null) {
                oldItem.setDescription(newItem.getDescription());
            }
            if (newItem.getAvailable() != null) {
                oldItem.setAvailable(newItem.getAvailable());
            }
            return oldItem;
        }
        throw new NotFoundException("Пользователь с id = " + newItem.getId() + " не найден");
    }
}
