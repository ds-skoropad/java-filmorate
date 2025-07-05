package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;

public interface UserFriendStorage {

    void friendAdd(Long id, Long friendId);

    void friendRemove(Long id, Long friendId);

    Collection<User> friendFindAll(Long id);

    Collection<User> friendCommon(Long id, Long otherId);

}