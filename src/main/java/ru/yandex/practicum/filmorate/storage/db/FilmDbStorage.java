package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Primary
@Repository
public class FilmDbStorage extends BaseDbStorage<Film> implements FilmStorage {

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Film create(Film film) {
        String sql = """
                INSERT INTO film (name, description, release_date, duration, mpa_id)
                VALUES (?, ?, ?, ?, ?)
                """;
        Long id = baseInsert(
                sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaId()
        );
        film.setId(id);
        log.trace("Table film INSERT: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = """
                UPDATE film SET name = ?, description = ?, release_date = ?, duration = ?, mpa_id = ?
                WHERE film_id = ?
                """;
        baseUpdate(
                sql,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpaId(),
                film.getId()
        );
        log.trace("Table film UPDATE: {}", film);
        return film;
    }

    @Override
    public Collection<Film> findAll() {
        String sql = """
                SELECT *
                FROM film
                """;
        return baseFindMany(sql);
    }

    @Override
    public Optional<Film> findById(Long filmId) {
        String sql = """
                SELECT *
                FROM film
                WHERE film_id = ?
                """;
        return baseFindOne(sql, filmId);
    }
}