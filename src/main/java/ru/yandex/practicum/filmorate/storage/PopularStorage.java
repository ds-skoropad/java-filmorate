package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface PopularStorage {

    void likeOn(Long filmId, Long userId);

    void likeOff(Long filmId, Long userId);

    Collection<Film> findPopular(Long count);
}
