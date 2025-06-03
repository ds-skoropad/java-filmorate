package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class FilmStorageTest<V extends FilmStorage> {
    V filmStorage;

    public void setUp(V filmStorage) {
        this.filmStorage = filmStorage;
    }

    @Test
    void createShouldBeCorrect() {
        final Film film = filmStorage.create(new Film(1L, "Name", "Description",
                LocalDate.of(2000, 1, 1), 100));

        assertEquals(film, filmStorage.findById(film.getId()));
    }

    @Test
    void updateShouldBeCorrect() {
        final Film createFilm = filmStorage.create(new Film(1L, "Name", "Description",
                LocalDate.of(2000, 1, 1), 100));
        final Film updateFilm = filmStorage.update(new Film(1L, "Name 2", "Description 2",
                LocalDate.of(2002, 2, 2), 200));

        assertEquals(updateFilm, filmStorage.findById(updateFilm.getId()));
    }

    @Test
    void findAllShouldBeCorrect() {
        final Film film1 = filmStorage.create(new Film(1L, "Name 1 ", "Description 1",
                LocalDate.of(2000, 1, 1), 100));
        final Film film2 = filmStorage.create(new Film(1L, "Name 2", "Description 2",
                LocalDate.of(2002, 2, 2), 200));

        assertEquals(List.of(film1, film2), filmStorage.findAll().stream().toList());
    }

    @Test
    void findByIdShouldBeCorrect() {
        final Film film = filmStorage.create(new Film(1L, "Name", "Description",
                LocalDate.of(2000, 1, 1), 100));

        assertEquals(film, filmStorage.findById(film.getId()));
    }

    @Test
    void containsIdShouldBeCorrect() {
        final Film film = filmStorage.create(new Film(1L, "Name", "Description",
                LocalDate.of(2000, 1, 1), 100));

        assertEquals(1L, film.getId());
        assertTrue(filmStorage.containsId(1L));
    }

    @Test
    void likeOnShouldBeCorrect() {
        final Film film = filmStorage.create(new Film(1L, "Name", "Description",
                LocalDate.of(2000, 1, 1), 100));

        filmStorage.likeOn(film.getId(), 1L);
        assertEquals(1, film.getLikes().size());
    }

    @Test
    void likeOffShouldBeCorrect() {
        final Film film = filmStorage.create(new Film(1L, "Name", "Description",
                LocalDate.of(2000, 1, 1), 100));
        film.getLikes().add(1L);

        filmStorage.likeOff(film.getId(), 1L);
        assertTrue(film.getLikes().isEmpty());
    }

    @Test
    void findPopularShouldBeCorrect() {
        final Film film1 = filmStorage.create(new Film(1L, "Name1", "Description1",
                LocalDate.of(2001, 1, 1), 100));
        final Film film2 = filmStorage.create(new Film(2L, "Name2", "Description2",
                LocalDate.of(2002, 2, 2), 200));
        film2.getLikes().add(1L);
        final Film film3 = filmStorage.create(new Film(3L, "Name3", "Description3",
                LocalDate.of(2003, 3, 3), 300));
        film3.getLikes().add(2L);
        film3.getLikes().add(3L);

        assertEquals(List.of(film3, film2, film1), filmStorage.findPopular(10L).stream().toList());
        assertEquals(List.of(film3, film2, film1), filmStorage.findPopular(3L).stream().toList());
        assertEquals(List.of(film3, film2), filmStorage.findPopular(2L).stream().toList());
    }

    // Exceptions

    @Test
    void findByIdShouldBeExceptionForNotFoundId() {
        assertThrows(NotFoundException.class, () -> filmStorage.findById(100L));
    }

    @Test
    void likeOnShouldBeExceptionForNotFoundId() {
        assertThrows(NotFoundException.class, () -> filmStorage.likeOn(100L, 1L));
    }

    @Test
    void likeOffShouldBeExceptionForNotFoundId() {
        assertThrows(NotFoundException.class, () -> filmStorage.likeOff(100L, 1L));
    }

    @Test
    void updateShouldBeExceptionForNotCorrectId() {
        final Film film = filmStorage.create(new Film(1L, "Name", "Description",
                LocalDate.of(2000, 1, 1), 100));
        film.setId(10L);

        assertThrows(NotFoundException.class, () -> filmStorage.update(film));
    }

    // Additional tests

    @Test
    void nextIdIsWorked() {
        final Film film = filmStorage.create(new Film(null, "Name", "Description",
                LocalDate.of(2000, 1, 1), 100));

        assertEquals(1L, film.getId());
    }
}