package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface FilmGenreStorage {

    void create(Long filmId, List<Integer> genreIds);

    List<Integer> update(Long filmId, List<Integer> genreIds);

    void delete(Long filmId, List<Integer> genreIds);

    Collection<Genre> findGenreByFilmId(Long filmId);

    Map<Long, List<Genre>> findAllGenreGroupByFilmId();

    Map<Long, List<Genre>> findGenreGroupByFilmId(List<Long> filmIds);
}
