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

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserFriendDbStorage.class, UserRowMapper.class})
class UserFriendDbStorageTest {
    private final JdbcTemplate jdbcTemplate;
    private final UserFriendDbStorage userFriendDbStorage;

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
                DELETE FROM user_friends;
                DELETE FROM users;
                ALTER TABLE users ALTER COLUMN user_id RESTART WITH 1;
                """;
        jdbcTemplate.update(sqlDown);
    }

    @Test
    void friendAdd() {
        userFriendDbStorage.friendAdd(1L, 2L);
        String sql = """
                SELECT *
                FROM user_friends
                """;
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);

        assertThat(result)
                .hasSize(1)
                .first()
                .hasToString("{USER_ID=1, FRIEND_ID=2}");
    }

    @Test
    void friendRemove() {
        String sqlFriendAdd = """
                INSERT INTO user_friends
                    (user_id, friend_id)
                VALUES
                    (1, 2);
                """;
        jdbcTemplate.update(sqlFriendAdd);

        userFriendDbStorage.friendRemove(1L, 2L);
        String sql = """
                SELECT *
                FROM user_friends
                """;
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);

        assertThat(result)
                .hasSize(0);
    }

    @Test
    void friendFindAll() {
        String sqlFriendAdd = """
                INSERT INTO user_friends
                    (user_id, friend_id)
                VALUES
                    (1, 2),
                    (1, 3),
                    (2, 1),
                    (2, 3),
                    (3, 1),
                    (3, 2);
                """;
        jdbcTemplate.update(sqlFriendAdd);

        Collection<User> friends = userFriendDbStorage.friendFindAll(1L);

        assertThat(friends)
                .hasSize(2)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(2L, 3L);
    }

    @Test
    void friendCommon() {
        String sqlFriendAdd = """
                INSERT INTO user_friends
                    (user_id, friend_id)
                VALUES
                    (1, 2),
                    (1, 3),
                    (2, 1),
                    (2, 3),
                    (3, 1),
                    (3, 2);
                """;
        jdbcTemplate.update(sqlFriendAdd);

        Collection<User> commonFriends = userFriendDbStorage.friendCommon(1L, 2L);

        assertThat(commonFriends)
                .hasSize(1)
                .extracting(User::getId)
                .containsExactlyInAnyOrder(3L);
    }
}