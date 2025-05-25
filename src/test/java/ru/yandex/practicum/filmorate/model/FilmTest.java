package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    @Test
    void allCorrect() {
        final Film film = new Film(1L, "Name", "Description", LocalDate.of(2000, 1, 1), 100);
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    void notCorrectFilmNameShouldBeBlank() {
        final Film film = new Film(1L, " ", "Description", LocalDate.of(2000, 1, 1), 100);
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals(List.of("Название фильма не может быть пустым"),
                violations.stream().map(ConstraintViolation::getMessage).toList());
    }

    @Test
    void notCorrectFilmDescriptionShouldBeMoreLength200() {
        final Film film = new Film(1L, "Name", "!".repeat(201), LocalDate.of(2000, 1, 1), 100);
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals(List.of("Описание фильма должно быть не более 200 символов"),
                violations.stream().map(ConstraintViolation::getMessage).toList());
    }

    @Test
    void notCorrectFilmReleaseDateShouldBeBefore() {
        final Film film = new Film(1L, "Name", "Description", LocalDate.of(1895, 12, 27), 100);
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals(List.of("Дата должна быть после 1895-12-28"),
                violations.stream().map(ConstraintViolation::getMessage).toList());
    }

    @Test
    void notCorrectFilmDurationShouldBeNotPositive() {
        final Film film = new Film(1L, "Name", "Description", LocalDate.of(2000, 1, 1), 0);
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals(List.of("Продолжительность фильма должна быть положительной"),
                violations.stream().map(ConstraintViolation::getMessage).toList());
    }

}