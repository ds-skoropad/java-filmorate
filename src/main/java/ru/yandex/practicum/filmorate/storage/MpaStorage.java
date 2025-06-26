package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface MpaStorage {

    Collection<Mpa> findAll();

    Optional<Mpa> findById(int id);

    Collection<Mpa> findByManyId(List<Integer> ids);

}
