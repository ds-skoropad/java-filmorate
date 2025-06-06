package ru.yandex.practicum.filmorate.storage.memory;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.storage.UserStorageTest;

class InMemoryUserStorageTest extends UserStorageTest<InMemoryUserStorage> {

    @BeforeEach
    void setUp() {
        super.setUp(new InMemoryUserStorage());
    }
}