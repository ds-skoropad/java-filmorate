package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Comparator;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FilmMapper {

    public static FilmDto mapToFilmDto(Film film, Collection<Genre> genres) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDuration(film.getDuration());
        filmDto.setMpa(film.getMpa());
        filmDto.setGenres(genres.stream().sorted(Comparator.comparing(Genre::getId)).toList());
        return filmDto;
    }

    public static Film mapToFilm(NewFilmRequest request) {
        Film film = new Film();
        film.setName(request.getName());
        film.setDescription(request.getDescription());
        film.setReleaseDate(request.getReleaseDate());
        film.setDuration(request.getDuration());
        film.setMpa(MpaMapper.mapToMpa(request.getMpa()));
        return film;
    }

    public static Film updateFilmFields(Film film, UpdateFilmRequest request) {
        Film newFilm = new Film(film.getId(), film.getName(), film.getDescription(),
                film.getReleaseDate(), film.getDuration(), film.getMpa());
        if (request.hasName()) {
            newFilm.setName(request.getName());
        }
        if (request.hasDescription()) {
            newFilm.setDescription(request.getDescription());
        }
        if (request.hasReleaseDate()) {
            newFilm.setReleaseDate(request.getReleaseDate());
        }
        if (request.hasDuration()) {
            newFilm.setDuration(request.getDuration());
        }
        if (request.hasMpa()) {
            newFilm.setMpa(MpaMapper.mapToMpa(request.getMpa()));
        }
        return newFilm;
    }
}