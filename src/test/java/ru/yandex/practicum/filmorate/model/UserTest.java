package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    void allCorrect() {
        final User user = new User(1L, "test@email.ru", "login", "name", LocalDate.of(2000, 1, 1));
        final Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    void notCorrectEmail() {
        final User user = new User(1L, "test-email.ru@", "login", "name", LocalDate.of(2000, 1, 1));
        final Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals(List.of("должно иметь формат адреса электронной почты"),
                violations.stream().map(ConstraintViolation::getMessage).toList());
    }

    @Test
    void notCorrectLoginShouldBeBlank() {
        final User user = new User(1L, "test@email.ru", " ", "name", LocalDate.of(2000, 1, 1));
        final Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals(List.of("Логин пользователя не может быть пустым и содержать пробелы"),
                violations.stream().map(ConstraintViolation::getMessage).toList());
    }

    @Test
    void notCorrectUserBirthdayShouldBeFuture() {
        final User user = new User(1L, "test@email.ru", "login", "name", LocalDate.now().plusYears(1));
        final Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals(List.of("Дата рождения пользователя не может быть в будущем"),
                violations.stream().map(ConstraintViolation::getMessage).toList());
    }

    @Test
    void userNameMayBeBlank() {
        final User user = new User(1L, "test@email.ru", "login", "", LocalDate.now().plusYears(1));
        final UserController userController = new UserController();
        final User newUser = userController.create(user);

        assertEquals("login", newUser.getName());
    }
}