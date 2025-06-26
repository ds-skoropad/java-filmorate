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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.db.mappers.FilmRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmDbStorage.class, FilmRowMapper.class})
class FilmDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final FilmDbStorage filmDbStorage;
    private final FilmRowMapper filmRowMapper;

    private final String sqlGetById = """
            SELECT *
            FROM film
            WHERE film_id = ?
            """;

    @BeforeEach
    void setUp() {
        String sqlUp = """
                INSERT INTO film
                    (name, description, release_date, duration, mpa_id)
                VALUES
                    ('name1', 'description1', '2001-01-01', 100, 1),
                    ('name2', 'description2', '2002-01-01', 200, 2),
                    ('name3', 'description3', '2003-01-01', 300, 3);
                """;
        jdbcTemplate.update(sqlUp);
    }

    @AfterEach
    void tearDown() {
        String sqlDown = """
                DELETE FROM film;
                ALTER TABLE film ALTER COLUMN film_id RESTART WITH 1;
                """;
        jdbcTemplate.update(sqlDown);
    }

    @Test
    void create() {
        final Film expectedFilm = new Film(0L, "new_name", "new_description", LocalDate.of(2004, 4, 4),
                400, 4);
        filmDbStorage.create(expectedFilm);

        final Film actualFilm = jdbcTemplate.queryForObject(sqlGetById, filmRowMapper, 4L);

        assertThat(actualFilm)
                .usingRecursiveComparison().isEqualTo(expectedFilm);
    }

    @Test
    void update() {
        final Film expectedFilm = new Film(2L, "update_name", "update_description", LocalDate.of(2005, 5, 5),
                400, 4);
        filmDbStorage.update(expectedFilm);

        final Film actualFilm = jdbcTemplate.queryForObject(sqlGetById, filmRowMapper, 2L);

        assertThat(actualFilm)
                .usingRecursiveComparison().isEqualTo(expectedFilm);
    }

    @Test
    void findAll() {
        Collection<Film> films = filmDbStorage.findAll();

        assertThat(films)
                .hasSize(3)
                .extracting(Film::getName)
                .containsExactly("name1", "name2", "name3");
    }

    @Test
    void findById() {
        Optional<Film> filmOptional = filmDbStorage.findById(2L);

        assertThat(filmOptional)
                .isPresent()
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("id", 2L)
                                .hasFieldOrPropertyWithValue("name", "name2")
                );
    }
}