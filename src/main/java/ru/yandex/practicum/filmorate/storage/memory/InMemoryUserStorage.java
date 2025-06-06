package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private Long currentId = 0L;

    @Override
    public User create(User user) {
        user.setId(++currentId);
        users.put(currentId, user);
        log.debug("Create user: id={}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        log.debug("Update user: id={}", user.getId());
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void friendAdd(Long id, Long friendId) {
        users.get(id).getFriends().add(friendId);
        users.get(friendId).getFriends().add(id);
        log.debug("Add friends: Id={} friendId={}", id, friendId);
    }

    @Override
    public void friendRemove(Long id, Long friendId) {
        users.get(id).getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(id);
        log.debug("Remove friends: Id={} friendId={}", id, friendId);
    }

    @Override
    public Collection<User> friendFindAll(Long id) {
        return users.get(id).getFriends().stream()
                .map(users::get)
                .toList();
    }

    @Override
    public Collection<User> friendCommon(Long id, Long otherId) {
        Set<Long> friendIds = users.get(id).getFriends();
        Set<Long> otherIds = users.get(otherId).getFriends();
        return friendIds.stream()
                .filter(otherIds::contains)
                .map(users::get)
                .toList();
    }
}