package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Path;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UpdateUserRequestTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    UpdateUserRequest updateUserRequest;

    @BeforeEach
    void setUp() {
        updateUserRequest = new UpdateUserRequest();
        updateUserRequest.setId(0L);
    }

    @Test
    void allCorrect() {
        final Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(updateUserRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    void notCorrectIdShouldBeNull() {
        updateUserRequest.setId(null);
        final Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(updateUserRequest);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("id", getFirstFieldNameViolations(violations));
    }

    @Test
    void notCorrectEmail() {
        updateUserRequest.setEmail("test-email.ru");
        final Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(updateUserRequest);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("email", getFirstFieldNameViolations(violations));
    }

    @Test
    void notCorrectLoginShouldBeContainWhiteSpaces() {
        updateUserRequest.setLogin("lo gin");
        final Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(updateUserRequest);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("login", getFirstFieldNameViolations(violations));
    }

    @Test
    void notCorrectBirthdayShouldBeFuture() {
        updateUserRequest.setBirthday(LocalDate.now().plusYears(1));
        final Set<ConstraintViolation<UpdateUserRequest>> violations = validator.validate(updateUserRequest);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("birthday", getFirstFieldNameViolations(violations));
    }

    // Additional Methods

    <V> String getFirstFieldNameViolations(Set<ConstraintViolation<V>> violations) {
        return violations.stream()
                .map(ConstraintViolation::getPropertyPath)
                .findFirst()
                .map(Path::toString).orElse(null);
    }
}