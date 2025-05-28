package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.MapUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(MapUtils.nextId(users));
        users.put(user.getId(), user);
        log.info("Создан пользователь id={}", user.getId());
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
        if (user.getId() == null) {
            log.warn("Не удалось обновить пользователя: id=null");
            throw new ValidException("Не удалось обновить пользователя: id=null");
        }
        if (!users.containsKey(user.getId())) {
            log.warn("Не удалось обновить пользователя: id не найден id={}", user.getId());
            throw new NotFoundException("Пользователь с id = " + user.getId() + "не найден");
        }

        User currentUser = users.get(user.getId());
        if (user.getEmail() == null) {
            user.setName(currentUser.getName());
        } else if (!user.getEmail().matches("^[\\w-\\.]+@[\\w-]+(\\.[\\w-]+)*\\.[a-z]{2,}$")) {
            log.warn("Не удалось обновить пользователя: не верный email id={}", user.getId());
            throw new ValidException("Не верный email");
        }
        if (user.getLogin() == null) {
            user.setLogin(currentUser.getLogin());
        } else if (user.getLogin().isBlank()) {
            log.warn("Не удалось обновить пользователя: не верный логин id={}", user.getId());
            throw new ValidException("Не верный логин");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday() == null) {
            user.setBirthday(currentUser.getBirthday());
        } else if (user.getBirthday().isAfter(LocalDate.now())) {
            log.warn("Не удалось обновить пользователя: не верная дата рождения id={}", user.getId());
            throw new ValidException("Не верная дата рождения");
        }

        users.put(user.getId(), user);
        log.info("Обновлен пользователь id={}", user.getId());
        return user;
    }

    @GetMapping
    public Collection<User> getAll() {
        return users.values();
    }

}
