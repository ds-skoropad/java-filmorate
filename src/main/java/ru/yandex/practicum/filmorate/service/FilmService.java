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
            throw new NotFoundException("Film not found: id = null");
        }
        if (!filmStorage.containsId(film.getId())) {
            throw new NotFoundException(String.format("Film not found: id=%s", film.getId()));
        }

        Film currentFilm = filmStorage.findById(film.getId());
        if (film.getName() == null) film.setName(currentFilm.getName());
        if (film.getDescription() == null) film.setDescription(currentFilm.getDescription());
        if (film.getReleaseDate() == null) film.setReleaseDate(currentFilm.getReleaseDate());
        if (film.getDuration() == 0) film.setDuration(currentFilm.getDuration());
        if (film.getLikes() == null) film.setLikes(currentFilm.getLikes());

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
        return filmStorage.findById(id);
    }

    public void likeOn(Long filmId, Long userId) {
        if (!userStorage.containsId(userId)) {
            throw new NotFoundException(String.format("User not found: id = %s", userId));
        }
        filmStorage.likeOn(filmId, userId);
    }

    public void likeOff(Long filmId, Long userId) {
        if (!userStorage.containsId(userId)) {
            throw new NotFoundException(String.format("User not found: id = %s", userId));
        }
        filmStorage.likeOff(filmId, userId);
    }

    public Collection<Film> findPopular(Long count) {
        return filmStorage.findPopular(count);
    }
}
