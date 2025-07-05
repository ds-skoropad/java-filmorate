package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmLikeStorage;

import java.util.Collection;

@Slf4j
@Primary
@Repository
@RequiredArgsConstructor
public class FilmLikeDbStorage implements FilmLikeStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<Film> filmRowMapper;

    @Override
    public void likeOn(Long filmId, Long userId) {
        String sql = """
                INSERT INTO film_likes (film_id, user_id)
                VALUES (?, ?)
                """;
        jdbc.update(sql, filmId, userId);
        log.trace("DB: INSERT INTO film_likes: film_id = {}, user_id = {}", filmId, userId);
    }

    @Override
    public void likeOff(Long filmId, Long userId) {
        String sql = """
                DELETE FROM film_likes
                WHERE film_id = ?
                    AND user_id = ?
                """;
        jdbc.update(sql, filmId, userId);
        log.trace("DB: DELETE FROM film_likes: film_id = {}, user_id = {}", filmId, userId);
    }

    @Override
    public Collection<Film> findPopular(Long count) {
        String sql = """
                SELECT f.film_id,
                    f.name,
                    f.description,
                    f.release_date,
                    f.duration,
                    f.mpa_id,
                    m.name AS mpa_name,
                    COUNT(p.user_id) AS count_like
                FROM film_likes AS p
                INNER JOIN films AS f
                    ON p.film_id = f.film_id
                LEFT OUTER JOIN mpa AS m
                    ON f.mpa_id = m.mpa_id
                GROUP BY p.film_id
                ORDER BY count_like DESC
                LIMIT ?
                """;
        return jdbc.query(sql, filmRowMapper, count);
    }
}