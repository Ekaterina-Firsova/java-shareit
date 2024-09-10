package ru.practicum.shareit.common;

import java.util.Map;

public class IdGenerator {

    public static <T> long getNextId(Map<Long, T> map) {
        long currentMaxId = map.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

}
