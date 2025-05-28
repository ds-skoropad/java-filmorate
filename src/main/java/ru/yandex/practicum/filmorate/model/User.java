package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    private Long id;
    @Email(message = "должно иметь формат адреса электронной почты")
    private String email;
    @NotBlank(message = "Логин пользователя не может быть пустым и содержать пробелы")
    private String login;
    private String name;
    @Past(message = "Дата рождения пользователя не может быть в будущем")
    private LocalDate birthday;
}
