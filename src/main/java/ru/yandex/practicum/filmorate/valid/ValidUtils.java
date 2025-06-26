package ru.yandex.practicum.filmorate.valid;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import ru.yandex.practicum.filmorate.exception.NotValidException;

import java.util.Set;

public final class ValidUtils {
    private ValidUtils() {
    }

    public static <T> void valid(T request, Validator validator) {
        Set<ConstraintViolation<T>> violations = validator.validate(request);
        if (!violations.isEmpty()) {
            StringBuilder message = new StringBuilder();
            for (ConstraintViolation<T> violation : violations) {
                message.append(String.format("%s = '%s' %s ",
                        violation.getPropertyPath(), violation.getInvalidValue(), violation.getMessage()));
            }
            throw new NotValidException("Validation failed: " + message);
        }
    }
}
