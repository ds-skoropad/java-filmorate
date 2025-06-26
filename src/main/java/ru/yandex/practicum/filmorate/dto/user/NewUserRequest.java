package ru.yandex.practicum.filmorate.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class NewUserRequest {
    @Email
    private String email;
    @Pattern(regexp = "\\S+")
    private String login;
    private String name;
    @Past
    private LocalDate birthday;
}
