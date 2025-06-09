package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
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
    void updateShouldBeExceptionForNullId() {
        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));
        userStorage.create(user);
        user.setId(null);

        assertThrows(NotValidException.class, () -> userService.update(user));
    }

    @Test
    void updateShouldNotBeExceptionForAllCorrect() {
        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));
        userStorage.create(user);

        assertDoesNotThrow(() -> userService.update(user));
    }

    @Test
    void updateShouldBeExceptionForNotCorrectEmail() {
        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));
        userStorage.create(user);
        user.setEmail("test.domain-ru");

        assertThrows(NotValidException.class, () -> userService.update(user));
    }

    @Test
    void updateShouldBeExceptionForNotCorrectLogin() {
        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));
        userStorage.create(user);
        user.setLogin(" ");

        assertThrows(NotValidException.class, () -> userService.update(user));
    }

    @Test
    void updateShouldBeExceptionForNotCorrectBirthday() {
        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));
        userStorage.create(user);
        user.setBirthday(LocalDate.now().plusYears(1));

        assertThrows(NotValidException.class, () -> userService.update(user));
    }

    @Test
    void updateUserNameMayBeBlank() {
        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));
        user.setName("");
        userService.update(user);

        assertEquals("login", user.getName());
    }

    // NotFound exceptions

    @Test
    void updateShouldBeExceptionForNotCorrectId() {
        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));
        user.setId(100L);

        assertThrows(NotFoundException.class, () -> userService.update(user));
    }

    @Test
    void findByIdShouldBeExceptionForNotFoundId() {
        assertThrows(NotFoundException.class, () -> userService.findById(100L));
    }

    @Test
    void friendAddShouldBeExceptionNotFoundId() {
        assertThrows(NotFoundException.class, () -> userService.friendAdd(1L, 2L));

        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));

        assertThrows(NotFoundException.class, () -> userService.friendAdd(1L, 2L));
    }

    @Test
    void friendAddShouldBeExceptionForSomeId() {
        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));

        assertThrows(NotValidException.class, () -> userService.friendAdd(1L, 1L));
    }

    @Test
    void friendRemoveShouldBeExceptionNotFoundId() {
        assertThrows(NotFoundException.class, () -> userService.friendRemove(1L, 2L));

        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));

        assertThrows(NotFoundException.class, () -> userService.friendRemove(1L, 2L));
    }

    @Test
    void friendRemoveShouldBeExceptionForSomeId() {
        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));

        assertThrows(NotValidException.class, () -> userService.friendRemove(1L, 1L));
    }

    @Test
    void friendFindAllShouldBeExceptionForNotFoundId() {
        assertThrows(NotFoundException.class, () -> userService.friendFindAll(1L));
    }

    @Test
    void friendCommonShouldBeExceptionForNotFoundId() {
        assertThrows(NotFoundException.class, () -> userService.friendCommon(1L, 2L));

        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));

        assertThrows(NotFoundException.class, () -> userService.friendCommon(1L, 2L));
    }

    @Test
    void friendCommonShouldBeExceptionForSomeId() {
        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));

        assertThrows(NotValidException.class, () -> userService.friendCommon(1L, 1L));
    }
}