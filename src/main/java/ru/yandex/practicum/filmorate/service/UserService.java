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
import java.util.Optional;
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
            throw new NotValidException("Cannot be null: id=null");
        }

        Optional<User> currentUser = userStorage.findById(user.getId());

        if (currentUser.isEmpty()) {
            throw new NotFoundException(String.format("User not found: id=%s", user.getId()));
        }

        if (user.getEmail() == null) user.setEmail(currentUser.get().getEmail());
        if (user.getLogin() == null) user.setLogin(currentUser.get().getLogin());
        if (user.getName() == null) user.setName(currentUser.get().getName());
        if (user.getBirthday() == null) user.setBirthday(currentUser.get().getBirthday());
        user.getFriends().addAll(currentUser.get().getFriends());

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
        Optional<User> user = userStorage.findById(id);

        if (user.isEmpty()) {
            throw new NotFoundException(String.format("User not found: id=%s", id));
        }
        return user.get();
    }

    public void friendAdd(Long id, Long friendId) {
        if (userStorage.findById(id).isEmpty()) {
            throw new NotFoundException(String.format("User not found: id = %s", id));
        }
        if (userStorage.findById(friendId).isEmpty()) {
            throw new NotFoundException(String.format("User not found: id = %s", friendId));
        }
        if (id.equals(friendId)) {
            throw new NotValidException(String.format("Same id: id=%s, friendId=%s", id, friendId));
        }
        userStorage.friendAdd(id, friendId);
    }

    public void friendRemove(Long id, Long friendId) {
        if (userStorage.findById(id).isEmpty()) {
            throw new NotFoundException(String.format("User not found: id = %s", id));
        }
        if (userStorage.findById(friendId).isEmpty()) {
            throw new NotFoundException(String.format("User not found: id = %s", friendId));
        }
        if (id.equals(friendId)) {
            throw new NotValidException(String.format("Same id: id=%s, friendId=%s", id, friendId));
        }
        userStorage.friendRemove(id, friendId);
    }

    public Collection<User> friendFindAll(Long id) {
        if (userStorage.findById(id).isEmpty()) {
            throw new NotFoundException(String.format("User not found: id = %s", id));
        }
        return userStorage.friendFindAll(id);
    }

    public Collection<User> friendCommon(Long id, Long otherId) {
        if (userStorage.findById(id).isEmpty()) {
            throw new NotFoundException(String.format("User not found: id = %s", id));
        }
        if (userStorage.findById(otherId).isEmpty()) {
            throw new NotFoundException(String.format("User not found: id = %s", otherId));
        }
        if (id.equals(otherId)) {
            throw new NotValidException(String.format("Same id: id=%s, otherId=%s", id, otherId));
        }
        return userStorage.friendCommon(id, otherId);
    }
}