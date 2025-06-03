package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotValidException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;
import ru.yandex.practicum.filmorate.storage.memory.InMemoryUserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    public Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
    public UserService userService;
    public UserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new InMemoryUserStorage();
        userService = new UserService(validator, userStorage);
    }

    @Test
    void createUserNameMayBeBlank() {
        final User user = userService.create(new User(1L, "test@email.ru", "login", "",
                LocalDate.of(2000, 1, 1)));

        assertEquals("login", user.getName());
    }

    // Validate update

    @Test
    void updateShouldNotBeExceptionForAllCorrect() {
        final User user = userService.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));
        userService.create(user);

        assertDoesNotThrow(() -> userService.update(user));
    }

    @Test
    void updateShouldBeExceptionForNotCorrectEmail() {
        final User user = userService.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));
        userService.create(user);
        user.setEmail("test.domain-ru");

        assertThrows(NotValidException.class, () -> userService.update(user));
    }

    @Test
    void updateShouldBeExceptionForNotCorrectLogin() {
        final User user = userService.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));
        userService.create(user);
        user.setLogin(" ");

        assertThrows(NotValidException.class, () -> userService.update(user));
    }

    @Test
    void updateShouldBeExceptionForNotCorrectBirthday() {
        final User user = userService.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));
        userService.create(user);
        user.setBirthday(LocalDate.now().plusYears(1));

        assertThrows(NotValidException.class, () -> userService.update(user));
    }

    @Test
    void updateUserNameMayBeBlank() {
        final User user = userService.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));
        user.setName("");
        userService.update(user);

        assertEquals("login", user.getName());
    }
}