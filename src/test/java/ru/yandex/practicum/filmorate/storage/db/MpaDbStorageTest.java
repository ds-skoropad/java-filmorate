package ru.yandex.practicum.filmorate.storage.db;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.db.mappers.MpaRowMapper;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaDbStorage.class, MpaRowMapper.class})
class MpaDbStorageTest {

    private final MpaDbStorage mpaDbStorage;

    @Test
    void findAll() {
        Collection<Mpa> mpas = mpaDbStorage.findAll();

        assertThat(mpas)
                .hasSize(5)
                .extracting(Mpa::getName)
                .containsExactly("G", "PG", "PG-13", "R", "NC-17");
    }

    @Test
    void findById() {
        Optional<Mpa> mpaOptional = mpaDbStorage.findById(2);

        assertThat(mpaOptional)
                .isPresent()
                .hasValueSatisfying(mpa ->
                        assertThat(mpa)
                                .hasFieldOrPropertyWithValue("id", 2)
                                .hasFieldOrPropertyWithValue("name", "PG")
                );
    }

    @Test
    void findByManyId() {
        Collection<Mpa> mpas = mpaDbStorage.findByManyId(List.of(2, 3, 4));

        assertThat(mpas)
                .hasSize(3)
                .extracting(Mpa::getName)
                .containsExactly("PG", "PG-13", "R");
    }
}