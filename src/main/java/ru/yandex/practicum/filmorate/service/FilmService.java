package ru.yandex.practicum.filmorate.service;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.NotValidException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final Validator validator;

    public Film create(Film film) {
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (film.getId() == null) {
            throw new NotValidException("Cannot be null: id = null");
        }

        Optional<Film> currentFilm = filmStorage.findById(film.getId());

        if (currentFilm.isEmpty()) {
            throw new NotFoundException(String.format("Film not found: id = %s", film.getId()));
        }

        if (film.getName() == null) film.setName(currentFilm.get().getName());
        if (film.getDescription() == null) film.setDescription(currentFilm.get().getDescription());
        if (film.getReleaseDate() == null) film.setReleaseDate(currentFilm.get().getReleaseDate());
        if (film.getDuration() == 0) film.setDuration(currentFilm.get().getDuration());
        film.getLikes().addAll(currentFilm.get().getLikes());

        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (ConstraintViolation<Film> violation : violations) {
                message.append(String.format("%s = '%s' %s ",
                        violation.getPropertyPath(), violation.getInvalidValue(), violation.getMessage()));
            }
            throw new NotValidException("Validation failed: " + message);
        }

        return filmStorage.update(film);
    }

    public Collection<Film> findAll() {
        return filmStorage.findAll();
    }

    public Film findById(Long id) {
        Optional<Film> film = filmStorage.findById(id);

        if (film.isEmpty()) {
            throw new NotFoundException(String.format("Film not found: id = %s", id));
        }
        return film.get();
    }

    public void likeOn(Long filmId, Long userId) {
        if (filmStorage.findById(filmId).isEmpty()) {
            throw new NotFoundException(String.format("Film not found: id = %s", filmId));
        }
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException(String.format("User not found: id = %s", userId));
        }
        filmStorage.likeOn(filmId, userId);
    }

    public void likeOff(Long filmId, Long userId) {
        if (filmStorage.findById(filmId).isEmpty()) {
            throw new NotFoundException(String.format("Film not found: id = %s", filmId));
        }
        if (userStorage.findById(userId).isEmpty()) {
            throw new NotFoundException(String.format("User not found: id = %s", userId));
        }
        filmStorage.likeOff(filmId, userId);
    }

    public Collection<Film> findPopular(Long count) {
        return filmStorage.findPopular(count);
    }
}