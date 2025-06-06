package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class FilmTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void createAllCorrect() {
        final Film film = new Film(1L, "Name", "Description",
                LocalDate.of(2000, 1, 1), 100);
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    void createNotCorrectFilmNameShouldBeBlank() {
        final Film film = new Film(1L, " ", "Description",
                LocalDate.of(2000, 1, 1), 100);
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("name", getFirstFieldNameViolations(violations));
    }

    @Test
    void createNotCorrectFilmDescriptionShouldBeMoreLength200() {
        final Film film = new Film(1L, "Name", "!".repeat(201),
                LocalDate.of(2000, 1, 1), 100);
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("description", getFirstFieldNameViolations(violations));
    }

    @Test
    void createNotCorrectFilmReleaseDateShouldBeBefore() {
        final Film film = new Film(1L, "Name", "Description",
                LocalDate.of(1895, 12, 27), 100);
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("releaseDate", getFirstFieldNameViolations(violations));
    }

    @Test
    void createNotCorrectFilmDurationShouldBeNotPositive() {
        final Film film = new Film(1L, "Name", "Description",
                LocalDate.of(2000, 1, 1), 0);
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("duration", getFirstFieldNameViolations(violations));
    }

    // Additional Methods

    <V> String getFirstFieldNameViolations(Set<ConstraintViolation<V>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getPropertyPath)
                .findFirst()
                .map(Path::toString).orElse(null);
    }
}