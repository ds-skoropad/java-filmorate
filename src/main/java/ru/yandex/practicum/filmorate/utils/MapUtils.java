package ru.yandex.practicum.filmorate.utils;

import java.util.Map;

public final class MapUtils {

    private MapUtils() {
    }

    public static <V> Long nextId(Map<Long, V> map) {
        long currentMaxId = map.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
