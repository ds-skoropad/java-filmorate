package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class MpaDbStorage extends BaseDbStorage<Mpa> implements MpaStorage {

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Mpa> findAll() {
        String sql = """
                SELECT *
                FROM mpa
                """;
        return baseFindMany(sql);
    }

    @Override
    public Optional<Mpa> findById(int mpaId) {
        String sql = """
                SELECT *
                FROM mpa
                WHERE mpa_id = ?
                """;
        return baseFindOne(sql, mpaId);
    }

    @Override
    public Collection<Mpa> findByManyId(List<Integer> ids) {
        String sql = """
                SELECT *
                FROM mpa
                WHERE mpa_id IN (%s)
                """;
        String inSql = String.join(",", Collections.nCopies(ids.size(), "?"));
        return baseFindMany(String.format(sql, inSql), ids.toArray());
    }
}