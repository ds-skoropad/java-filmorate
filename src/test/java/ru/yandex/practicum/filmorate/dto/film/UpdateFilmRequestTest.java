package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UpdateFilmRequestTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    UpdateFilmRequest updateFilmRequest;

    @BeforeEach
    void setUp() {
        updateFilmRequest = new UpdateFilmRequest();
        updateFilmRequest.setId(0L);
    }

    @Test
    void allCorrect() {
        final Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(updateFilmRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    void notCorrectNameShouldBeBlank() {
        updateFilmRequest.setName(" ");
        final Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(updateFilmRequest);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("name", getFirstFieldNameViolations(violations));
    }

    @Test
    void notCorrectDescriptionShouldBeLengthMore200() {
        updateFilmRequest.setDescription("!".repeat(201));
        final Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(updateFilmRequest);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("description", getFirstFieldNameViolations(violations));
    }

    @Test
    void notCorrectReleaseDateShouldBeBefore() {
        updateFilmRequest.setReleaseDate(LocalDate.of(1895, 12, 27));
        final Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(updateFilmRequest);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("releaseDate", getFirstFieldNameViolations(violations));
    }

    @Test
    void notCorrectDurationShouldBeNotPositive() {
        updateFilmRequest.setDuration(0);
        final Set<ConstraintViolation<UpdateFilmRequest>> violations = validator.validate(updateFilmRequest);

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