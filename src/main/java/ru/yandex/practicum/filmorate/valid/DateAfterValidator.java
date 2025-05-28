package ru.yandex.practicum.filmorate.valid;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateAfterValidator implements ConstraintValidator<DateAfter, LocalDate> {
    private LocalDate minimumDate;

    @Override
    public void initialize(DateAfter constraintAnnotation) {
        minimumDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        return value == null || !value.isBefore(minimumDate);
    }
}