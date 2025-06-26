package ru.yandex.practicum.filmorate.storage.db;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Primary
@Repository
public class GenreDbStorage extends BaseDbStorage<Genre> implements GenreStorage {

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Genre> findAll() {
        String sql = """
                SELECT *
                FROM genre
                """;
        return baseFindMany(sql);
    }

    @Override
    public Optional<Genre> findById(int genreId) {
        String sql = """
                SELECT *
                FROM genre
                WHERE genre_id = ?
                """;
        return baseFindOne(sql, genreId);
    }

    @Override
    public Collection<Genre> findByManyId(List<Integer> genreIds) {
        String sql = """
                SELECT *
                FROM genre
                WHERE genre_id IN (%s)
                """;
        String inSql = String.join(",", Collections.nCopies(genreIds.size(), "?"));
        return baseFindMany(String.format(sql, inSql), genreIds.toArray());
    }
}