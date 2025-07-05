package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.db.mappers.GenreRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmGenreDbStorage.class, GenreRowMapper.class})
class FilmGenreDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final FilmGenreDbStorage filmGenreDbStorage;

    private static final String SQL_FIND_BY_FILM_ID = """
            SELECT genre_id
            FROM film_genres
            WHERE film_id = ?
            """;

    @BeforeEach
    void setUp() {
        String sqlUp = """
                INSERT INTO films
                    (name, description, release_date, duration, mpa_id)
                VALUES
                    ('name1', 'description1', '2001-01-01', 100, 1),
                    ('name2', 'description2', '2002-01-01', 200, 2),
                    ('name3', 'description3', '2003-01-01', 300, 3);
                INSERT INTO film_genres
                    (film_id, genre_id)
                VALUES
                    (1, 1),
                    (1, 2),
                    (2, 1),
                    (2, 2),
                    (2, 3);
                """;
        jdbcTemplate.update(sqlUp);
    }

    @AfterEach
    void tearDown() {
        String sqlDown = """
                DELETE FROM film_genres;
                DELETE FROM films;
                ALTER TABLE films ALTER COLUMN film_id RESTART WITH 1;
                """;
        jdbcTemplate.update(sqlDown);
    }

    @Test
    void create() {
        filmGenreDbStorage.create(1L, List.of(3, 4));
        List<Integer> result = jdbcTemplate.queryForList(SQL_FIND_BY_FILM_ID, Integer.class, 1L);

        assertThat(result)
                .hasSize(4)
                .containsExactlyInAnyOrder(1, 2, 3, 4);
    }

    @Test
    void update() {
        filmGenreDbStorage.update(1L, List.of(2, 3));
        List<Integer> result = jdbcTemplate.queryForList(SQL_FIND_BY_FILM_ID, Integer.class, 1L);

        assertThat(result)
                .hasSize(2)
                .containsExactlyInAnyOrder(2, 3);
    }

    @Test
    void delete() {
        filmGenreDbStorage.delete(1L, List.of(1, 2));
        List<Integer> result = jdbcTemplate.queryForList(SQL_FIND_BY_FILM_ID, Integer.class, 1L);

        assertThat(result)
                .hasSize(0);
    }

    @Test
    void findAllGenreGroupByFilmId() {
        List<Genre> genresFilm1 = List.of(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма"));
        List<Genre> genresFilm2 = List.of(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"));

        Map<Long, List<Genre>> result = filmGenreDbStorage.findAllGenreGroupByFilmId();

        assertThat(result)
                .hasSize(2)
                .containsEntry(1L, genresFilm1)
                .containsEntry(2L, genresFilm2);
    }

    @Test
    void findGenreGroupByFilmId() {
        List<Genre> genresFilm1 = List.of(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма"));

        Map<Long, List<Genre>> result = filmGenreDbStorage.findGenreGroupByFilmId(List.of(1L));

        assertThat(result)
                .hasSize(1)
                .containsEntry(1L, genresFilm1);
    }

    @Test
    void findGenreByFilmId() {
        List<Genre> genresFilm2 = List.of(
                new Genre(1, "Комедия"),
                new Genre(2, "Драма"),
                new Genre(3, "Мультфильм"));

        Collection<Genre> result = filmGenreDbStorage.findGenreByFilmId(2L);

        assertThat(result)
                .isEqualTo(genresFilm2);
    }
}