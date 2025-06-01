package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidException;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    // Create tests

    @Test
    void createAllCorrect() {
        final Film film = new Film(1L, "Name", "Description", LocalDate.of(2000, 1, 1), 100);
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    void createNotCorrectFilmNameShouldBeBlank() {
        final Film film = new Film(1L, " ", "Description", LocalDate.of(2000, 1, 1), 100);
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals(List.of("Название фильма не может быть пустым"),
                violations.stream().map(ConstraintViolation::getMessage).toList());
    }

    @Test
    void createNotCorrectFilmDescriptionShouldBeMoreLength200() {
        final Film film = new Film(1L, "Name", "!".repeat(201), LocalDate.of(2000, 1, 1), 100);
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals(List.of("Описание фильма должно быть не более 200 символов"),
                violations.stream().map(ConstraintViolation::getMessage).toList());
    }

    @Test
    void createNotCorrectFilmReleaseDateShouldBeBefore() {
        final Film film = new Film(1L, "Name", "Description", LocalDate.of(1895, 12, 27), 100);
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals(List.of("Дата должна быть после 1895-12-28"),
                violations.stream().map(ConstraintViolation::getMessage).toList());
    }

    @Test
    void createNotCorrectFilmDurationShouldBeNotPositive() {
        final Film film = new Film(1L, "Name", "Description", LocalDate.of(2000, 1, 1), 0);
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals(List.of("Продолжительность фильма должна быть положительной"),
                violations.stream().map(ConstraintViolation::getMessage).toList());
    }

    // Update tests

    @Test
    void updateAllCorrect() {
        final Film film = new Film(1L, "Name", "Description", LocalDate.of(2000, 1, 1), 100);
        final FilmController filmController = new FilmController();
        filmController.create(film);
        final Film newFilm = filmController.update(film);

        assertEquals(film, newFilm);
    }

    @Test
    void updateNotCorrectId() {
        final Film film = new Film(1L, "Name", "Description", LocalDate.of(2000, 1, 1), 100);
        final FilmController filmController = new FilmController();
        filmController.create(film);
        film.setId(10L);

        Assertions.assertThrows(NotFoundException.class, () -> filmController.update(film));
    }

    @Test
    void updateNotCorrectFilmNameShouldBeBlank() {
        final Film film = new Film(1L, "Name", "Description", LocalDate.of(2000, 1, 1), 100);
        final FilmController filmController = new FilmController();
        filmController.create(film);
        film.setName(" ");

        Assertions.assertThrows(ValidException.class, () -> filmController.update(film));
    }

    @Test
    void updateNotCorrectFilmDescriptionShouldBeMoreLength200() {
        final Film film = new Film(1L, "Name", "Description", LocalDate.of(2000, 1, 1), 100);
        final FilmController filmController = new FilmController();
        filmController.create(film);
        film.setDescription("!".repeat(201));

        Assertions.assertThrows(ValidException.class, () -> filmController.update(film));
    }

    @Test
    void updateNotCorrectFilmReleaseDateShouldBeBefore() {
        final Film film = new Film(1L, "Name", "Description", LocalDate.of(2000, 1, 1), 100);
        final FilmController filmController = new FilmController();
        filmController.create(film);
        film.setReleaseDate(LocalDate.of(1895, 12, 27));

        Assertions.assertThrows(ValidException.class, () -> filmController.update(film));
    }

    @Test
    void updateNotCorrectFilmDurationShouldBeNotPositive() {
        final Film film = new Film(1L, "Name", "Description", LocalDate.of(2000, 1, 1), 100);
        final FilmController filmController = new FilmController();
        filmController.create(film);
        film.setDuration(-1);

        Assertions.assertThrows(ValidException.class, () -> filmController.update(film));
    }

}