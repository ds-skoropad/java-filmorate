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

class UserTest {
    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1));
    }

    @Test
    void createAllCorrect() {
        final Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    void createNotCorrectEmail() {
        user.setEmail("test-email.ru@");
        final Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("email", getFirstFieldNameViolations(violations));
    }

    @Test
    void createNotCorrectLoginShouldBeContainWhiteSpaces() {
        user.setLogin("lo gin");
        final Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals("login", getFirstFieldNameViolations(violations));
    }

    @Test
    void createNotCorrectUserBirthdayShouldBeFuture() {
        user.setBirthday(LocalDate.now().plusYears(1));
        final Set<ConstraintViolation<User>> violations = validator.validate(user);

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