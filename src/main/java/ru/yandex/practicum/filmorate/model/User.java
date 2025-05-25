package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(exclude = {"id"})
@AllArgsConstructor
public class User {
    Long id;
    @Email(message = "должно иметь формат адреса электронной почты")
    String email;
    @NotBlank(message = "Логин пользователя не может быть пустым и содержать пробелы")
    String login;
    String name;
    @Past(message = "Дата рождения пользователя не может быть в будущем")
    LocalDate birthday;
}
