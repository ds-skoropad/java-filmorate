package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NotValidException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {
    private final Validator validator;
    private final UserStorage userStorage;

    public User create(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User update(User user) {
        if (user.getId() == null) {
            throw new NotFoundException("User not found: id=null");
        }
        if (!userStorage.containsId(user.getId())) {
            throw new NotFoundException(String.format("User not found: id=%s", user.getId()));
        }

        User currentFilm = userStorage.findById(user.getId());
        if (user.getEmail() == null) user.setEmail(currentFilm.getEmail());
        if (user.getLogin() == null) user.setLogin(currentFilm.getLogin());
        if (user.getName() == null) user.setName(currentFilm.getName());
        if (user.getBirthday() == null) user.setBirthday(currentFilm.getBirthday());
        if (user.getFriends() == null) user.setFriends(currentFilm.getFriends());

        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }

        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (ConstraintViolation<User> violation : violations) {
                message.append(String.format("%s = '%s' %s ",
                        violation.getPropertyPath(), violation.getInvalidValue(), violation.getMessage()));
            }
            throw new NotValidException("Validation failed: " + message);
        }

        userStorage.update(user);
        return userStorage.update(user);
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User findById(Long id) {
        return userStorage.findById(id);
    }

    public void friendAdd(Long id, Long friendId) {
        userStorage.friendAdd(id, friendId);
    }

    public void friendRemove(Long id, Long friendId) {
        userStorage.friendRemove(id, friendId);
    }

    public Collection<User> friendFindAll(Long id) {
        return userStorage.friendFindAll(id);
    }

    public Collection<User> friendCommon(Long id, Long otherId) {
        return userStorage.friendCommon(id, otherId);
    }
}

