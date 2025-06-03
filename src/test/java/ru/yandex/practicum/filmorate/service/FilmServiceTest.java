package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmServiceTest {

    public Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    public FilmStorage filmStorage;
    public FilmService filmService;

    @BeforeEach
    void setUp() {
        UserStorage userStorage = new InMemoryUserStorage();
        filmStorage = new InMemoryFilmStorage();
        filmService = new FilmService(filmStorage, userStorage, validator);
    }

    // Validate likes

    @Test
    void likeOnShouldBeExceptionForNotFoundUserId() {
        assertThrows(NotFoundException.class, () -> filmService.likeOn(1L, 100L));
    }

    @Test
    void likeOffShouldBeExceptionForNotFoundUserId() {
        assertThrows(NotFoundException.class, () -> filmService.likeOff(1L, 100L));
    }

    // Validate update

    @Test
    void updateShouldNotBeExceptionForAllCorrect() {
        final Film film = new Film(1L, "Name", "Description",
                LocalDate.of(2000, 1, 1), 100);
        filmStorage.create(film);

        assertDoesNotThrow(() -> filmService.update(film));
    }

    @Test
    void updateShouldBeExceptionForNotCorrectId() {
        final Film film = new Film(1L, "Name", "Description",
                LocalDate.of(2000, 1, 1), 100);
        filmStorage.create(film);
        film.setId(10L);

        assertThrows(NotFoundException.class, () -> filmService.update(film));
    }

    @Test
    void updateShouldBeExceptionFotNotCorrectFilmName() {
        final Film film = new Film(1L, "Name", "Description",
                LocalDate.of(2000, 1, 1), 100);
        filmStorage.create(film);
        film.setName(" ");

        assertThrows(NotValidException.class, () -> filmService.update(film));
    }

    @Test
    void updateShouldBeExceptionForNotCorrectFilmDescription() {
        final Film film = new Film(1L, "Name", "Description",
                LocalDate.of(2000, 1, 1), 100);
        filmStorage.create(film);
        film.setDescription("!".repeat(201));

        assertThrows(NotValidException.class, () -> filmService.update(film));
    }

    @Test
    void updateShouldBeExceptionFotNotCorrectFilmReleaseDate() {
        final Film film = new Film(1L, "Name", "Description",
                LocalDate.of(2000, 1, 1), 100);
        filmStorage.create(film);
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        assertThrows(NotValidException.class, () -> filmService.update(film));
    }

    @Test
    void updateShouldBeExceptionFotNotCorrectFilmDuration() {
        final Film film = new Film(1L, "Name", "Description",
                LocalDate.of(2000, 1, 1), 100);
        filmStorage.create(film);
        film.setDuration(-1);

        assertThrows(NotValidException.class, () -> filmService.update(film));
    }
}