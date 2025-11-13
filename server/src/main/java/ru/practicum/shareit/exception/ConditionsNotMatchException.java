package ru.practicum.shareit.exception;

public class ConditionsNotMatchException extends RuntimeException {
    public ConditionsNotMatchException(String message) {
        super(message);
    }
}
