package ru.yandex.practicum.filmorate.exceptions;

public class NotFoundExceptionEntity extends RuntimeException {
    public NotFoundExceptionEntity(String message) {
        super(message);
    }
}