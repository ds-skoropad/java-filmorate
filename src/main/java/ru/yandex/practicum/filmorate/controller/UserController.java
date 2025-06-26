package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.NewUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UpdateUserRequest;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public Collection<UserDto> getAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDto findById(@PathVariable Long id) {
        return userService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest newUserRequest) {
        return userService.create(newUserRequest);
    }

    @PutMapping
    public UserDto update(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        return userService.update(updateUserRequest);
    }

    @GetMapping("/{id}/friends")
    public Collection<UserDto> friendFindAll(@PathVariable Long id) {
        return userService.friendFindAll(id);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Collection<UserDto> friendCommon(@PathVariable Long id, @PathVariable Long otherId) {
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
