package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.FilmGenreStorage;
import ru.yandex.practicum.filmorate.storage.db.mappers.GenreRowMapper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Primary
@Repository
@RequiredArgsConstructor
public class FilmGenreDbStorage implements FilmGenreStorage {
    private final JdbcTemplate jdbcTemplate;
    private final GenreRowMapper genreRowMapper;
    private final ResultSetExtractor<Map<Long, List<Genre>>> mapExtractor = new ResultSetExtractor<>() {
        @Override
        public Map<Long, List<Genre>> extractData(ResultSet rs) throws SQLException {
            Map<Long, List<Genre>> data = new HashMap<>();
            while (rs.next()) {
                Long filmId = rs.getLong("film_id");
                data.putIfAbsent(filmId, new ArrayList<>());
                Genre genre = new Genre();
                genre.setId(rs.getInt("genre_id"));
                genre.setName(rs.getString("name"));
                data.get(filmId).add(genre);
            }
            return data;
        }
    };

    @Override
    public void create(Long filmId, List<Integer> genreIds) {
        String sql = """
                INSERT INTO film_genre (film_id, genre_id)
                VALUES (?, ?)
                """;
        if (!genreIds.isEmpty()) {
            jdbcTemplate.batchUpdate(
                    sql,
                    genreIds,
                    100,
                    (PreparedStatement ps, Integer genreId) -> {
                        ps.setLong(1, filmId);
                        ps.setInt(2, genreId);

                    });
            log.trace("TABLE film_genre INSERT: film_id = {}, genreIds = {}", filmId, genreIds);
        }
    }

    @Override
    public List<Integer> update(Long filmId, List<Integer> genreIds) {
        String sql = """
                SELECT genre_id
                FROM film_genre
                WHERE film_id = ?
                """;

        List<Integer> currentGenreIds = jdbcTemplate.queryForList(sql, Integer.class, filmId);
        ;
        List<Integer> deleteGenreIds = currentGenreIds.stream()
                .filter(g -> !genreIds.contains(g))
                .toList();
        List<Integer> insertGenreIds = genreIds.stream()
                .filter(g -> !currentGenreIds.contains(g))
                .toList();

        if (!deleteGenreIds.isEmpty()) {
            delete(filmId, deleteGenreIds);
        }
        if (!insertGenreIds.isEmpty()) {
            create(filmId, insertGenreIds);
        }

        log.trace("TABLE film_genre UPDATE: film_id = {}, genreIds = {}", filmId, genreIds);
        return genreIds;
    }

    @Override
    public void delete(Long filmId, List<Integer> genreIds) {
        String sql = """
                DELETE FROM film_genre
                WHERE film_id = ?
                    AND genre_id IN (%s)
                """;

        String inSql = String.join(",", Collections.nCopies(genreIds.size(), "?"));
        Stream<Long> params = Stream.concat(Stream.of(filmId), genreIds.stream().map(Long::valueOf));

        jdbcTemplate.update(String.format(sql, inSql), params.toArray());
        log.trace("TABLE film_genre DELETE: film_id = {}, genreIds = {}", filmId, genreIds);
    }

    @Override
    public Map<Long, List<Genre>> findAllGenreGroupByFilmId() {
        String sql = """
                SELECT fg.film_id,
                    g.genre_id,
                    g.name
                FROM film_genre AS fg
                LEFT JOIN genre AS g
                    ON fg.genre_id = g.genre_id
                """;
        return jdbcTemplate.query(sql, mapExtractor);
    }

    @Override
    public Map<Long, List<Genre>> findGenreGroupByFilmId(List<Long> filmIds) {
        String sql = """
                SELECT fg.film_id,
                    g.genre_id,
                    g.name
                FROM film_genre AS fg
                LEFT JOIN genre AS g
                    ON fg.genre_id = g.genre_id
                WHERE fg.film_id IN (%s)
                """;
        String inSql = String.join(",", Collections.nCopies(filmIds.size(), "?"));
        return jdbcTemplate.query(String.format(sql, inSql), mapExtractor, filmIds.toArray());
    }


    @Override
    public Collection<Genre> findGenreByFilmId(Long filmId) {
        String sql = """
                SELECT *
                FROM genre
                WHERE genre_id IN (
                    SELECT genre_id
                    FROM film_genre
                    WHERE film_id = ?
                )
                """;
        return jdbcTemplate.query(sql, genreRowMapper, filmId);
    }
}