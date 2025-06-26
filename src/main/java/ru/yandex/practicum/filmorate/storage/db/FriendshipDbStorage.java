package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FriendshipStorage;

import java.util.Collection;

@Slf4j
@Primary
@Repository
@RequiredArgsConstructor
public class FriendshipDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbc;
    private final RowMapper<User> userRowMapper;

    @Override
    public void friendAdd(Long id, Long friendId) {
        String sql = """
                INSERT INTO friendship (person_id, friend_id)
                VALUES (?, ?)
                """;
        jdbc.update(sql, id, friendId);
        log.trace("Database friendship INSERT: person_id = {}, friend_id = {}", id, friendId);
    }

    @Override
    public void friendRemove(Long id, Long friendId) {
        String sql = """
                DELETE FROM friendship
                WHERE person_id = ?
                    AND friend_id = ?
                """;
        jdbc.update(sql, id, friendId);
        log.trace("Database friendship DELETE: person_id = {}, friend_id = {}", id, friendId);
    }

    @Override
    public Collection<User> friendFindAll(Long id) {
        String sql = """
                SELECT *
                FROM person
                WHERE person_id IN (
                    SELECT friend_id
                    FROM friendship
                    WHERE person_id = ?
                )
                """;
        return jdbc.query(sql, userRowMapper, id);
    }

    @Override
    public Collection<User> friendCommon(Long id, Long otherId) {
        String sql = """
                SELECT *
                FROM person
                WHERE person_id IN (
                    SELECT friend_id
                    FROM friendship
                    WHERE person_id IN (?, ?)
                    GROUP BY friend_id
                    HAVING count(friend_id) = 2
                )
                """;
        return jdbc.query(sql, userRowMapper, id, otherId);
    }
}