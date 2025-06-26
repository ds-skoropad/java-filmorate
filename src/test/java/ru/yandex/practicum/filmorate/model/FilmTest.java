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
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    Film film;

    @BeforeEach
    void setUp() {
        film = new Film(1L, "Name", "Description",
                LocalDate.of(2000, 1, 1), 100, 1);
    }

    @Test
    void createAllCorrect() {
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertTrue(violations.isEmpty());
    }

    @Test
    void createNotCorrectFilmNameShouldBeBlank() {
        film.setName(" ");
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("name", getFirstFieldNameViolations(violations));
    }

    @Test
    void createNotCorrectFilmDescriptionShouldBeMoreLength200() {
        film.setDescription("!".repeat(201));
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("description", getFirstFieldNameViolations(violations));
    }

    @Test
    void createNotCorrectFilmReleaseDateShouldBeBefore() {
        film.setReleaseDate(LocalDate.of(1895, 12, 27));
        final Set<ConstraintViolation<Film>> violations = validator.validate(film);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("releaseDate", getFirstFieldNameViolations(violations));
    }

    @Test
    void createNotCorrectFilmDurationShouldBeNotPositive() {
        film.setDuration(0);
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