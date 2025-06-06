package ru.yandex.practicum.filmorate.exception;

public class NotValidException extends IllegalArgumentException {
    public NotValidException(String message) {
        super(message);
    }
}
