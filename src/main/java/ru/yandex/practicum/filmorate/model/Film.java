package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.valid.DateAfter;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(exclude = {"id"})
@AllArgsConstructor
public class Film {
    Long id;
    @NotBlank(message = "Название фильма не может быть пустым")
    String name;
    @Size(max = 200, message = "Описание фильма должно быть не более 200 символов")
    String description;
    @DateAfter
    LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    int duration;
}
