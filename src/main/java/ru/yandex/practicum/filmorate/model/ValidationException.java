package ru.yandex.practicum.filmorate.model;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}