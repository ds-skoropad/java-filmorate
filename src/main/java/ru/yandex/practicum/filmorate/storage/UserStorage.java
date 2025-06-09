package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Optional;

public interface UserStorage {

    User create(User user);

    User update(User user);

    Collection<User> findAll();

    Optional<User> findById(Long id);

    void friendAdd(Long id, Long friendId);

    void friendRemove(Long id, Long friendId);

    Collection<User> friendFindAll(Long id);

    Collection<User> friendCommon(Long id, Long otherId);

}