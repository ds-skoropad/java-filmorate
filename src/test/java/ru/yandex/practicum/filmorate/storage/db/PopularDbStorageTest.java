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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({PopularDbStorage.class, FilmRowMapper.class})
class PopularDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final PopularDbStorage popularDbStorage;
    private final FilmRowMapper filmRowMapper;

    @BeforeEach
    void setUp() {
        String sqlUp = """
                INSERT INTO film
                    (name, description, release_date, duration, mpa_id)
                VALUES
                    ('name1', 'description1', '2001-01-01', 100, 1),
                    ('name2', 'description2', '2002-01-01', 200, 2),
                    ('name3', 'description3', '2003-01-01', 300, 3);
                INSERT INTO person
                    (email, login, name, birthday_date)
                VALUES
                    ('user1@domain.com', 'login1', 'name1', '2001-01-01'),
                    ('user2@domain.com', 'login2', 'name2', '2002-01-01'),
                    ('user3@domain.com', 'login3', 'name3', '2003-01-01');
                """;
        jdbcTemplate.update(sqlUp);
    }

    @AfterEach
    void tearDown() {
        String sqlDown = """
                DELETE FROM popular;
                DELETE FROM film;
                ALTER TABLE film ALTER COLUMN film_id RESTART WITH 1;
                DELETE FROM person;
                ALTER TABLE person ALTER COLUMN person_id RESTART WITH 1;
                """;
        jdbcTemplate.update(sqlDown);
    }

    @Test
    void likeOn() {
        popularDbStorage.likeOn(1L, 1L);
        String sql = """
                SELECT *
                FROM popular
                """;
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);

        assertThat(result)
                .hasSize(1)
                .first()
                .hasToString("{FILM_ID=1, PERSON_ID=1}");
    }

    @Test
    void likeOff() {
        String sqlLikeOn = """
                INSERT INTO popular
                    (film_id, person_id)
                VALUES
                    (1, 1);
                """;
        jdbcTemplate.update(sqlLikeOn);

        popularDbStorage.likeOff(1L, 1L);
        String sql = """
                SELECT *
                FROM popular
                """;
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);

        assertThat(result)
                .hasSize(0);
    }

    @Test
    void findPopular() {
        String sqlLikeOn = """
                INSERT INTO popular
                    (film_id, person_id)
                VALUES
                    (3, 1),
                    (3, 2),
                    (2, 1),
                    (2, 2),
                    (2, 3),
                    (1, 1);
                """;
        jdbcTemplate.update(sqlLikeOn);

        Collection<Film> popular100 = popularDbStorage.findPopular(100L);

        assertThat(popular100)
                .hasSize(3)
                .extracting(Film::getName)
                .containsExactly("name2", "name3", "name1");

        Collection<Film> popular2 = popularDbStorage.findPopular(2L);

        assertThat(popular2)
                .hasSize(2)
                .extracting(Film::getName)
                .containsExactly("name2", "name3");
    }
}