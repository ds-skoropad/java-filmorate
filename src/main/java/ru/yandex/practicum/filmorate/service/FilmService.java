package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequest;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.db.MpaDbStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final Validator validator;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MpaDbStorage mpaDbStorage;
    private final GenreStorage genreStorage;
    private final FilmGenreStorage filmGenreStorage;
    private final PopularStorage popularStorage;

    public FilmDto create(NewFilmRequest request) {
        List<Genre> genres;
        List<Integer> genreIds;

        Mpa mpa = mpaDbStorage.findById(request.getMpa().getId()).orElseThrow(
                () -> new NotFoundException(String.format("Mpa not found: id = %s", request.getMpa().getId())));

        if (request.hasGenre()) {
            genreIds = request.getGenres().stream()
                    .map(GenreDto::getId)
                    .toList();
            genres = getValidGenres(genreIds);
        } else {
            genres = List.of();
            genreIds = null;
        }

        Film film = FilmMapper.mapToFilm(request);
        film.setMpa(mpa);
        film = filmStorage.create(film);

        if (request.hasGenre()) {
            filmGenreStorage.create(film.getId(), genreIds);
        }

        return FilmMapper.mapToFilmDto(film, genres);
    }

    public FilmDto update(UpdateFilmRequest request) {
        List<Genre> genres;

        Film currentFilm = filmStorage.findById(request.getId()).orElseThrow(
                () -> new NotFoundException(String.format("Film not found: id = %s", request.getId())));
        Film commonFilm = FilmMapper.updateFilmFields(currentFilm, request);

        if (request.hasMpa()) {
            int mpaId = request.getMpa().getId();
            Mpa mpa = mpaDbStorage.findById(mpaId).orElseThrow(
                    () -> new NotFoundException(String.format("Mpa not found: id = %s", mpaId)));
            commonFilm.setMpa(mpa);
        }
        if (request.hasGenre()) {
            List<Integer> genreIds = request.getGenres().stream()
                    .map(GenreDto::getId)
                    .toList();
            genres = getValidGenres(genreIds);
            filmGenreStorage.update(commonFilm.getId(), genreIds);
        } else {
            genres = filmGenreStorage.findGenreByFilmId(commonFilm.getId()).stream().toList();
        }

        currentFilm = filmStorage.update(commonFilm);

        return FilmMapper.mapToFilmDto(currentFilm, genres);
    }

    public Collection<FilmDto> findAll() {
        Collection<Film> films = filmStorage.findAll();
        Map<Long, List<Genre>> groupGenres = filmGenreStorage.findAllGenreGroupByFilmId();

        return getManyFilmDto(films, groupGenres);
    }

    public FilmDto findById(Long id) {
        Film film = filmStorage.findById(id).orElseThrow(
                () -> new NotFoundException(String.format("Film not found: id = %s", id)));
        Collection<Genre> genres = filmGenreStorage.findGenreByFilmId(id);

        return FilmMapper.mapToFilmDto(film, genres);
    }

    public void likeOn(Long filmId, Long userId) {
        filmStorage.findById(filmId).orElseThrow(
                () -> new NotFoundException(String.format("Film not found: id = %s", filmId)));
        userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User not found: id = %s", userId)));
        popularStorage.likeOn(filmId, userId);
    }

    public void likeOff(Long filmId, Long userId) {
        filmStorage.findById(filmId).orElseThrow(
                () -> new NotFoundException(String.format("Film not found: id = %s", filmId)));
        userStorage.findById(userId).orElseThrow(
                () -> new NotFoundException(String.format("User not found: id = %s", userId)));
        popularStorage.likeOff(filmId, userId);
    }

    public Collection<FilmDto> findPopular(Long count) {
        Collection<Film> films = popularStorage.findPopular(count);
        List<Long> filmIds = films.stream()
                .map(Film::getId)
                .toList();
        Map<Long, List<Genre>> groupGenres = filmGenreStorage.findGenreGroupByFilmId(filmIds);

        return getManyFilmDto(films, groupGenres);
    }

    // Additional methods

    private List<Genre> getValidGenres(List<Integer> genreIds) {
        List<Genre> genres = genreStorage.findByManyId(genreIds).stream().toList();
        List<Integer> validGenreIds = genres.stream()
                .map(Genre::getId)
                .toList();
        List<Integer> diffGenreIds = genreIds.stream()
                .filter(g -> !validGenreIds.contains(g))
                .toList();
        if (!diffGenreIds.isEmpty()) {
            throw new NotFoundException(String.format("Genre not found: id = %s", diffGenreIds));
        }
        return genres;
    }

    private Collection<FilmDto> getManyFilmDto(Collection<Film> films, Map<Long, List<Genre>> groupGenres) {
        Collection<FilmDto> filmsDto = new ArrayList<>();
        Collection<Genre> genres;

        for (Film film : films) {
            genres = groupGenres.containsKey(film.getId()) ? groupGenres.get(film.getId()) : List.of();
            filmsDto.add(FilmMapper.mapToFilmDto(film, genres));
        }

        return filmsDto;
    }
}