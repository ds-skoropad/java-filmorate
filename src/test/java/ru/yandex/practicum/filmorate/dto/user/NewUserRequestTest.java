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

class NewUserRequestTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    NewUserRequest newUserRequest;

    @BeforeEach
    void setUp() {
        newUserRequest = new NewUserRequest();
        newUserRequest.setEmail("email@domain.com");
        newUserRequest.setLogin("login");
        newUserRequest.setBirthday(LocalDate.now().minusYears(20));
    }

    @Test
    void allCorrect() {
        final Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(newUserRequest);

        assertTrue(violations.isEmpty());
    }

    @Test
    void notCorrectEmail() {
        newUserRequest.setEmail("test-email.ru@");
        final Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(newUserRequest);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("email", getFirstFieldNameViolations(violations));
    }

    @Test
    void notCorrectLoginShouldBeContainWhiteSpaces() {
        newUserRequest.setLogin("lo gin");
        final Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(newUserRequest);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("login", getFirstFieldNameViolations(violations));
    }

    @Test
    void notCorrectBirthdayShouldBeFuture() {
        newUserRequest.setBirthday(LocalDate.now().plusYears(1));
        final Set<ConstraintViolation<NewUserRequest>> violations = validator.validate(newUserRequest);

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