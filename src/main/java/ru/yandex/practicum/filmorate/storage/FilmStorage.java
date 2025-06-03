package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;

public interface FilmStorage {

    Film create(Film film);

    Film update(Film film);

    Collection<Film> findAll();

    Film findById(Long id);

    boolean containsId(Long id);

    void likeOn(Long filmId, Long userId);

    void likeOff(Long filmId, Long userId);

    Collection<Film> findPopular(Long count);

}
