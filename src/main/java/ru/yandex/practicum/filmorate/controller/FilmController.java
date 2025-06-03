package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<Film> getAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public Film findById(@PathVariable @NotNull Long id) {
        return filmService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@Valid @RequestBody Film film) {
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        return filmService.update(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeOn(@PathVariable @NotNull Long id, @PathVariable @NotNull Long userId) {
        filmService.likeOn(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void likeOff(@PathVariable Long id, @PathVariable Long userId) {
        filmService.likeOff(id, userId);
    }

    @GetMapping("/popular")
    public Collection<Film> findPopular(@RequestParam(required = false, defaultValue = "10") Long count) {
        return filmService.findPopular(count);
    }
}
