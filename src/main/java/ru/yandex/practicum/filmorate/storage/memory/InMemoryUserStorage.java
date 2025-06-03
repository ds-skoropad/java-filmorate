package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NotValidException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException(String.format("User not found: id=%s", user.getId()));
        }
        users.put(user.getId(), user);
        log.debug("Update user: id={}", user.getId());
        return user;
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User findById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("User not found: id = %s", id));
        }
        return users.get(id);
    }

    @Override
    public boolean containsId(Long id) {
        return users.containsKey(id);
    }

    @Override
    public void friendAdd(Long id, Long friendId) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("User not found: id = %s", id));
        }
        if (!users.containsKey(friendId)) {
            throw new NotFoundException(String.format("User not found: id = %s", friendId));
        }
        if (id.equals(friendId)) {
            throw new NotValidException(String.format("Same id: id=%s, friendId=%s", id, friendId));
        }
        users.get(id).getFriends().add(friendId);
        users.get(friendId).getFriends().add(id);
        log.debug("Add friends: Id={} friendId={}", id, friendId);
    }

    @Override
    public void friendRemove(Long id, Long friendId) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("User not found: id = %s", id));
        }
        if (!users.containsKey(friendId)) {
            throw new NotFoundException(String.format("User not found: id = %s", friendId));
        }
        if (id.equals(friendId)) {
            throw new NotValidException(String.format("Same id: id=%s, friendId=%s", id, friendId));
        }
        users.get(id).getFriends().remove(friendId);
        users.get(friendId).getFriends().remove(id);
        log.debug("Remove friends: Id={} friendId={}", id, friendId);
    }

    @Override
    public Collection<User> friendFindAll(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("User not found: id = %s", id));
        }
        return users.get(id).getFriends().stream()
                .map(users::get)
                .toList();
    }

    @Override
    public Collection<User> friendCommon(Long id, Long otherId) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("User not found: id = %s", id));
        }
        if (!users.containsKey(otherId)) {
            throw new NotFoundException(String.format("User not found: id = %s", otherId));
        }
        if (id.equals(otherId)) {
            throw new NotValidException(String.format("Same id: id=%s, otherId=%s", id, otherId));
        }
        Set<Long> friendIds = users.get(id).getFriends();
        Set<Long> otherIds = users.get(otherId).getFriends();
        return friendIds.stream()
                .filter(otherIds::contains)
                .map(users::get)
                .toList();
    }
}
