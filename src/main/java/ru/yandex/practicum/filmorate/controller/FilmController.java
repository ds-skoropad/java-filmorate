package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;

@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public Collection<FilmDto> getAll() {
        return filmService.findAll();
    }

    @GetMapping("/{id}")
    public FilmDto findById(@PathVariable Long id) {
        return filmService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FilmDto create(@Valid @RequestBody NewFilmRequest newFilmRequest) {
        return filmService.create(newFilmRequest);
    }

    @PutMapping
    public FilmDto update(@Valid @RequestBody UpdateFilmRequest updateFilmRequest) {
        return filmService.update(updateFilmRequest);
    }

    @PutMapping("/{id}/like/{userId}")
    public void likeOn(@PathVariable Long id, @PathVariable Long userId) {
        filmService.likeOn(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void likeOff(@PathVariable Long id, @PathVariable Long userId) {
        filmService.likeOff(id, userId);
    }

    @GetMapping("/popular")
    public Collection<FilmDto> findPopular(@RequestParam(required = false, defaultValue = "10") Long count) {
        return filmService.findPopular(count);
    }
}
