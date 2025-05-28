package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidException;

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

    // Create tests

    @Test
    void createAllCorrect() {
        final User user = new User(1L, "test@email.ru", "login", "name", LocalDate.of(2000, 1, 1));
        final Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertTrue(violations.isEmpty());
    }

    @Test
    void createNotCorrectEmail() {
        final User user = new User(1L, "test-email.ru@", "login", "name", LocalDate.of(2000, 1, 1));
        final Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals(List.of("должно иметь формат адреса электронной почты"),
                violations.stream().map(ConstraintViolation::getMessage).toList());
    }

    @Test
    void createNotCorrectLoginShouldBeBlank() {
        final User user = new User(1L, "test@email.ru", " ", "name", LocalDate.of(2000, 1, 1));
        final Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals(List.of("Логин пользователя не может быть пустым и содержать пробелы"),
                violations.stream().map(ConstraintViolation::getMessage).toList());
    }

    @Test
    void createNotCorrectUserBirthdayShouldBeFuture() {
        final User user = new User(1L, "test@email.ru", "login", "name", LocalDate.now().plusYears(1));
        final Set<ConstraintViolation<User>> violations = validator.validate(user);

        assertFalse(violations.isEmpty());
        assertEquals(1, violations.size());
        assertEquals(List.of("Дата рождения пользователя не может быть в будущем"),
                violations.stream().map(ConstraintViolation::getMessage).toList());
    }

    @Test
    void createUserNameMayBeBlank() {
        final User user = new User(1L, "test@email.ru", "login", "", LocalDate.now().plusYears(1));
        final UserController userController = new UserController();
        final User newUser = userController.create(user);

        assertEquals("login", newUser.getName());
    }

    // Update tests

    @Test
    void updateAllCorrect() {
        final User user = new User(1L, "test@email.ru", "login", "name", LocalDate.of(2000, 1, 1));
        final UserController userController = new UserController();
        userController.create(user);
        final User newUser = userController.update(user);

        assertEquals(user, newUser);
    }

    @Test
    void updateNotCorrectId() {
        final User user = new User(1L, "test@email.ru", "login", "name", LocalDate.of(2000, 1, 1));
        final UserController userController = new UserController();
        userController.create(user);
        user.setId(10L);

        Assertions.assertThrows(NotFoundException.class, () -> userController.update(user));
    }

    @Test
    void updateNotCorrectEmail() {
        final User user = new User(1L, "test@email.ru", "login", "name", LocalDate.of(2000, 1, 1));
        final UserController userController = new UserController();
        userController.create(user);
        user.setEmail("test-email.ru@");

        Assertions.assertThrows(ValidException.class, () -> userController.update(user));
    }

    @Test
    void updateNotCorrectLoginShouldBeBlank() {
        final User user = new User(1L, "test@email.ru", "login", "name", LocalDate.of(2000, 1, 1));
        final UserController userController = new UserController();
        userController.create(user);
        user.setLogin(" ");

        Assertions.assertThrows(ValidException.class, () -> userController.update(user));
    }

    @Test
    void updateNotCorrectUserBirthdayShouldBeFuture() {
        final User user = new User(1L, "test@email.ru", "login", "name", LocalDate.of(2000, 1, 1));
        final UserController userController = new UserController();
        userController.create(user);
        user.setBirthday(LocalDate.now().plusYears(1));

        Assertions.assertThrows(ValidException.class, () -> userController.update(user));
    }

    @Test
    void updateUserNameMayBeBlank() {
        final User user = new User(1L, "test@email.ru", "login", "name", LocalDate.of(2000, 1, 1));
        final UserController userController = new UserController();
        userController.create(user);
        user.setName("");
        User updateUser = userController.update(user);

        assertEquals(user, updateUser);
    }
}