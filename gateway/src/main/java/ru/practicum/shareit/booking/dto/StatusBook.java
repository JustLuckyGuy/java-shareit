package ru.practicum.shareit.booking.dto;

import java.util.Optional;

public enum StatusBook {
    ALL,
    CURRENT,
    FUTURE,
    PAST,
    APPROVED,
    REJECTED,
    WAITING;

    public static Optional<StatusBook> from(String stringState) {
        for (StatusBook state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
