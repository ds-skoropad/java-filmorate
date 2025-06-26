package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.genre.GenreIdDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaIdDto;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateFilmRequest {
    @NotNull
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    private MpaIdDto mpa;
    private Set<GenreIdDto> genres;

    public boolean hasName() {
        return name != null;
    }

    public boolean hasDescription() {
        return description != null;
    }

    public boolean hasReleaseDate() {
        return releaseDate != null;
    }

    public boolean hasDuration() {
        return duration > 0;
    }

    public boolean hasMpa() {
        return mpa != null;
    }

    public boolean hasGenre() {
        return genres != null;
    }

}
