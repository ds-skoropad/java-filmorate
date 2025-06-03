package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserStorage {

    User create(User user);

    User update(User user);

    Collection<User> findAll();

    User findById(Long id);

    boolean containsId(Long id);

    void friendAdd(Long id, Long friendId);

    void friendRemove(Long id, Long friendId);

    Collection<User> friendFindAll(Long id);

    Collection<User> friendCommon(Long id, Long otherId);

}
