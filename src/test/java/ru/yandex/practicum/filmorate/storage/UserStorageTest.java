package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NotValidException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class UserStorageTest<V extends UserStorage> {
    V userStorage;

    public void setUp(V userStorage) {
        this.userStorage = userStorage;
    }

    @Test
    void createShouldBeCorrect() {
        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));

        assertEquals(user, userStorage.findById(user.getId()));
    }

    @Test
    void updateShouldBeCorrect() {
        final User createUser = userStorage.create(new User(1L, "test1@email.ru", "login1", "name1",
                LocalDate.of(2001, 1, 1)));
        final User updateUser = userStorage.create(new User(1L, "test2@email.ru", "login2", "name2",
                LocalDate.of(2002, 2, 2)));

        assertEquals(updateUser, userStorage.findById(updateUser.getId()));
    }

    @Test
    void findAllShouldBeCorrect() {
        final User user1 = userStorage.create(new User(1L, "test1@email.ru", "login1", "name1",
                LocalDate.of(2001, 1, 1)));
        final User user2 = userStorage.create(new User(1L, "test2@email.ru", "login2", "name2",
                LocalDate.of(2002, 2, 2)));

        assertEquals(List.of(user1, user2), userStorage.findAll().stream().toList());
    }

    @Test
    void findByIdShouldBeCorrect() {
        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));

        assertEquals(user, userStorage.findById(user.getId()));
    }

    @Test
    void containsIdShouldBeCorrect() {
        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));

        assertEquals(1L, user.getId());
        assertTrue(userStorage.containsId(1L));
    }

    @Test
    void friendAddShouldBeCorrect() {
        final User user1 = userStorage.create(new User(1L, "test1@email.ru", "login1", "name1",
                LocalDate.of(2001, 1, 1)));
        final User user2 = userStorage.create(new User(1L, "test2@email.ru", "login2", "name2",
                LocalDate.of(2002, 2, 2)));

        userStorage.friendAdd(user1.getId(), user2.getId());

        assertTrue(user1.getFriends().contains(user2.getId()));
        assertTrue(user2.getFriends().contains(user1.getId()));
    }

    @Test
    void friendRemoveShouldBeCorrect() {
        final User user1 = userStorage.create(new User(1L, "test1@email.ru", "login1", "name1",
                LocalDate.of(2001, 1, 1)));
        final User user2 = userStorage.create(new User(1L, "test2@email.ru", "login2", "name2",
                LocalDate.of(2002, 2, 2)));

        user1.getFriends().add(user2.getId());
        user2.getFriends().add(user1.getId());
        userStorage.friendRemove(user1.getId(), user2.getId());

        assertFalse(user1.getFriends().contains(user2.getId()));
        assertFalse(user2.getFriends().contains(user1.getId()));
    }

    @Test
    void friendFindAllShouldBeCorrect() {
        final User user1 = userStorage.create(new User(1L, "test1@email.ru", "login1", "name1",
                LocalDate.of(2001, 1, 1)));
        final User user2 = userStorage.create(new User(1L, "test2@email.ru", "login2", "name2",
                LocalDate.of(2002, 2, 2)));
        final User user3 = userStorage.create(new User(1L, "test3@email.ru", "login3", "name3",
                LocalDate.of(2003, 3, 3)));

        user1.getFriends().add(user2.getId());
        user1.getFriends().add(user3.getId());

        assertEquals(List.of(user2, user3),
                userStorage.friendFindAll(user1.getId()).stream().toList());
    }

    @Test
    void friendCommonShouldBeCorrect() {
        final User user1 = userStorage.create(new User(1L, "test1@email.ru", "login1", "name1",
                LocalDate.of(2001, 1, 1)));
        final User user2 = userStorage.create(new User(1L, "test2@email.ru", "login2", "name2",
                LocalDate.of(2002, 2, 2)));
        final User user3 = userStorage.create(new User(1L, "test3@email.ru", "login3", "name3",
                LocalDate.of(2003, 3, 3)));

        user1.getFriends().add(user2.getId());
        user1.getFriends().add(user3.getId());

        user3.getFriends().add(user1.getId());
        user3.getFriends().add(user2.getId());

        assertEquals(List.of(user2), userStorage.friendCommon(user1.getId(), user3.getId()));
    }

    // Exceptions

    @Test
    void findByIdShouldBeExceptionForNotFoundId() {
        assertThrows(NotFoundException.class, () -> userStorage.findById(100L));
    }

    @Test
    void updateShouldBeExceptionForNotCorrectId() {
        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));
        user.setId(10L);

        assertThrows(NotFoundException.class, () -> userStorage.update(user));
    }

    @Test
    void friendAddShouldBeExceptionNotFoundId() {
        assertThrows(NotFoundException.class, () -> userStorage.friendAdd(1L, 2L));

        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));

        assertThrows(NotFoundException.class, () -> userStorage.friendAdd(1L, 2L));
    }

    @Test
    void friendAddShouldBeExceptionForSomeId() {
        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));

        assertThrows(NotValidException.class, () -> userStorage.friendAdd(1L, 1L));
    }

    @Test
    void friendRemoveShouldBeExceptionNotFoundId() {
        assertThrows(NotFoundException.class, () -> userStorage.friendRemove(1L, 2L));

        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));

        assertThrows(NotFoundException.class, () -> userStorage.friendRemove(1L, 2L));
    }

    @Test
    void friendRemoveShouldBeExceptionForSomeId() {
        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));

        assertThrows(NotValidException.class, () -> userStorage.friendRemove(1L, 1L));
    }

    // Additional tests

    @Test
    void nextIdIsWorked() {
        final User user = userStorage.create(new User(1L, "test@email.ru", "login", "name",
                LocalDate.of(2000, 1, 1)));

        assertEquals(1L, user.getId());
    }
}