package ru.yandex.practicum.filmorate.storage.memory;

import org.junit.jupiter.api.BeforeEach;
import ru.yandex.practicum.filmorate.storage.FilmStorageTest;

class InMemoryFilmStorageTest extends FilmStorageTest<InMemoryFilmStorage> {

    @BeforeEach
    void setUp() {
        super.setUp(new InMemoryFilmStorage());
    }
}