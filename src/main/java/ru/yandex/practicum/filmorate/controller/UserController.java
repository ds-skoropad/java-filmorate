package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<User> getAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public User findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody User user) {
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody User user) {
        return userService.update(user);
    }

    @GetMapping("/{id}/friends")
    public Collection<User> friendFindAll(@PathVariable Long id) {
        return userService.friendFindAll(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Collection<User> friendCommon(@PathVariable Long id, @PathVariable Long otherId) {
        return userService.friendCommon(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void friendAdd(@PathVariable Long id, @PathVariable Long friendId) {
        userService.friendAdd(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void friendRemove(@PathVariable Long id, @PathVariable Long friendId) {
        userService.friendRemove(id, friendId);
    }
}
