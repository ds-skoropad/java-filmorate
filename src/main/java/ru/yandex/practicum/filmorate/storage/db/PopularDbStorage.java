package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.PopularStorage;

import java.util.Collection;

@Slf4j
@Primary
@Repository
@RequiredArgsConstructor
public class PopularDbStorage implements PopularStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<Film> filmRowMapper;

    @Override
    public void likeOn(Long filmId, Long userId) {
        String sql = """
                INSERT INTO popular (film_id, person_id)
                VALUES (?, ?)
                """;
        jdbc.update(sql, filmId, userId);
        log.trace("Table popular INSERT: film_id = {}, user_id = {}", filmId, userId);
    }

    @Override
    public void likeOff(Long filmId, Long userId) {
        String sql = """
                DELETE FROM popular
                WHERE film_id = ?
                    AND person_id = ?
                """;
        jdbc.update(sql, filmId, userId);
        log.trace("Table popular DELETE: film_id = {}, user_id = {}", filmId, userId);
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
                    COUNT(p.person_id) AS count_like
                FROM popular AS p
                INNER JOIN film AS f
                    ON p.film_id = f.film_id
                GROUP BY p.film_id
                ORDER BY count_like DESC
                LIMIT ?
                """;
        return jdbc.query(sql, filmRowMapper, count);
    }
}