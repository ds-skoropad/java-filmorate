package ru.yandex.practicum.filmorate.dto.film;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.dto.genre.GenreDto;
import ru.yandex.practicum.filmorate.dto.mpa.MpaDto;
import ru.yandex.practicum.filmorate.valid.DateAfter;

import java.time.LocalDate;
import java.util.Set;

@Data
public class UpdateFilmRequest {
    @NotNull
    private Long id;
    @Nullable
    @Pattern(regexp = "^(?!\\s*$).+", message = "must not be blank") // @Nullable + @NotBlank not work
    private String name;
    @Nullable
    @Size(max = 200)
    private String description;
    @Nullable
    @DateAfter
    private LocalDate releaseDate;
    @Nullable
    @Positive
    private Integer duration;
    private MpaDto mpa;
    private Set<GenreDto> genres;

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