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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.db.mappers.UserRowMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, UserRowMapper.class})
class UserDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final UserDbStorage userDbStorage;
    private final UserRowMapper userRowMapper;

    private static final String SQL_USER_GET_BY_ID = """
            SELECT *
            FROM users
            WHERE user_id = ?
            """;

    @BeforeEach
    void setUp() {
        String sqlUp = """
                INSERT INTO users
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
                DELETE FROM users;
                ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
                """;
        jdbcTemplate.update(sqlDown);
    }

    @Test
    void create() {
        final User expectedUser = new User(0L, "new@domain.com", "new_login", "new_name",
                LocalDate.of(2004, 4, 4));
        userDbStorage.create(expectedUser);

        final User actualUser = jdbcTemplate.queryForObject(SQL_USER_GET_BY_ID, userRowMapper, 4L);

        assertThat(actualUser)
                .usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    void update() {
        final User expectedUser = new User(2L, "update@domain.com", "update_login", "update_name",
                LocalDate.of(2005, 5, 5));
        userDbStorage.update(expectedUser);

        final User actualUser = jdbcTemplate.queryForObject(SQL_USER_GET_BY_ID, userRowMapper, 2L);

        assertThat(actualUser)
                .usingRecursiveComparison().isEqualTo(expectedUser);
    }

    @Test
    void findAll() {
        Collection<User> users = userDbStorage.findAll();

        assertThat(users)
                .hasSize(3)
                .extracting(User::getLogin)
                .containsExactly("login1", "login2", "login3");
    }

    @Test
    void findById() {
        Optional<User> userOptional = userDbStorage.findById(2L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", 2L)
                                .hasFieldOrPropertyWithValue("login", "login2")
                );
    }
}