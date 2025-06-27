package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dto.mpa.MpaIdDto;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class NewFilmRequestTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    NewFilmRequest newFilmRequest;

    @BeforeEach
    void setUp() {
        newFilmRequest = new NewFilmRequest();
        newFilmRequest.setName("name");
        newFilmRequest.setDescription("description");
        newFilmRequest.setReleaseDate(LocalDate.of(2000, 1, 1));
        newFilmRequest.setDuration(100);
        newFilmRequest.setMpa(new MpaIdDto());
        newFilmRequest.getMpa().setId(1);
    }

    @Test
    void allCorrect() {
        final Set<ConstraintViolation<NewFilmRequest>> violations = validator.validate(newFilmRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    void notCorrectNameShouldBeBlank() {
        newFilmRequest.setName(" ");
        final Set<ConstraintViolation<NewFilmRequest>> violations = validator.validate(newFilmRequest);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("name", getFirstFieldNameViolations(violations));
    }

    @Test
    void notCorrectDescriptionShouldBeLengthMore200() {
        newFilmRequest.setDescription("!".repeat(201));
        final Set<ConstraintViolation<NewFilmRequest>> violations = validator.validate(newFilmRequest);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("description", getFirstFieldNameViolations(violations));
    }

    @Test
    void notCorrectReleaseDateShouldBeBefore() {
        newFilmRequest.setReleaseDate(LocalDate.of(1895, 12, 27));
        final Set<ConstraintViolation<NewFilmRequest>> violations = validator.validate(newFilmRequest);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("releaseDate", getFirstFieldNameViolations(violations));
    }

    @Test
    void notCorrectDurationShouldBeNotPositive() {
        newFilmRequest.setDuration(0);
        final Set<ConstraintViolation<NewFilmRequest>> violations = validator.validate(newFilmRequest);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("duration", getFirstFieldNameViolations(violations));
    }

    @Test
    void notCorrectMpaShouldBeNull() {
        newFilmRequest.setMpa(null);
        final Set<ConstraintViolation<NewFilmRequest>> violations = validator.validate(newFilmRequest);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("mpa", getFirstFieldNameViolations(violations));
    }

    // Additional Methods

    <V> String getFirstFieldNameViolations(Set<ConstraintViolation<V>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getPropertyPath)
                .findFirst()
                .map(Path::toString).orElse(null);
    }
}