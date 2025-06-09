package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private Long currentId = 0L;

    @Override
    public Film create(Film film) {
        film.setId(++currentId);
        films.put(currentId, film);
        log.debug("Create film: id={}", film.getId());
        return film;
    }

    @Override
    public Film update(Film film) {
        films.put(film.getId(), film);
        log.debug("Update film: id={}", film.getId());
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Optional<Film> findById(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public void likeOn(Long filmId, Long userId) {
        films.get(filmId).getLikes().add(userId);
        log.debug("Add like: filmId={} userId={}", filmId, userId);
    }

    @Override
    public void likeOff(Long filmId, Long userId) {
        films.get(filmId).getLikes().remove(userId);
        log.debug("Remove like: filmId={} userId={}", filmId, userId);
    }

    @Override
    public Collection<Film> findPopular(Long count) {
        return films.values().stream()
                .sorted(Comparator.comparing(film -> film.getLikes().size(), Comparator.reverseOrder()))
                .limit(count)
                .toList();
    }
}