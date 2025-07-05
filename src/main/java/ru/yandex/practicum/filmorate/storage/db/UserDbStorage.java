package ru.yandex.practicum.filmorate.storage.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;

@Slf4j
@Primary
@Repository
public class UserDbStorage extends BaseDbStorage<User> implements UserStorage {

    public UserDbStorage(JdbcTemplate jdbc, RowMapper<User> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public User create(User user) {
        String sql = """
                INSERT INTO users (email, login, name, birthday_date)
                VALUES (?, ?, ?, ?)
                """;
        Long id = baseInsert(
                sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday()
        );
        user.setId(id);
        log.trace("DB: INSERT INTO users: {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        String sql = """
                UPDATE users SET email = ?, login = ?, name = ?, birthday_date = ?
                WHERE user_id = ?
                """;
        baseUpdate(
                sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId()
        );
        log.trace("DB: UPDATE users: {}", user);
        return user;
    }

    @Override
    public Collection<User> findAll() {
        String sql = """
                SELECT *
                FROM users
                """;
        return baseFindMany(sql);
    }

    @Override
    public Optional<User> findById(Long userId) {
        String sql = """
                SELECT *
                FROM users
                WHERE user_id = ?
                """;
        return baseFindOne(sql, userId);
    }
}