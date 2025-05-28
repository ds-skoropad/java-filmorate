package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.MapUtils;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        film.setId(MapUtils.nextId(films));
        films.put(film.getId(), film);
        log.info("Создан фильм id={}", film.getId());
        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) {
        if (film.getId() == null) {
            log.warn("Не удалось обновить фильм: id=null");
            throw new ValidException("Не удалось обновить фильм: id=null");
        }
        if (!films.containsKey(film.getId())) {
            log.warn("Не удалось обновить фильм: id не найден id={}", film.getId());
            throw new NotFoundException("Фильм с id = " + film.getId() + " не найден");
        }

        Film currentFilm = films.get(film.getId());
        if (film.getName() == null) {
            film.setName(currentFilm.getName());
        } else if (film.getName().isBlank()) {
            log.warn("Не удалось обновить фильм: пустое имя id={}", film.getId());
            throw new ValidException("Название фильма не может быть пустым");
        }
        if (film.getDescription() == null) {
            film.setDescription(currentFilm.getDescription());
        } else if (film.getDescription().length() > 200) {
            log.warn("Не удалось обновить фильм: длинное описание id={}", film.getId());
            throw new ValidException("Описание фильма должно быть не более 200 символов");
        }
        if (film.getReleaseDate() == null) {
            film.setReleaseDate(currentFilm.getReleaseDate());
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Не удалось обновить фильм: неверная дата релиза id={}", film.getId());
            throw new ValidException("Дата должна быть после 1895-12-27");
        }
        if (film.getDuration() == 0) {
            film.setDuration(currentFilm.getDuration());
        } else if (film.getDuration() < 0) {
            log.warn("Не удалось обновить фильм: отрицательная продолжительность id={}", film.getId());
            throw new ValidException("Продолжительность фильма должна быть положительной");
        }

        films.put(film.getId(), film);
        log.info("Обновлен фильм id={}", currentFilm.getId());
        return film;
    }

    @GetMapping
    public Collection<Film> getAll() {
        return films.values();
    }

}
