package ru.practicum.events.model;

import java.util.Optional;

public enum EventSort {
    EVENT_DATE,
    VIEWS;
    public static Optional<EventSort> from(String state) {
        for (EventSort value : EventSort.values()) {
            if (value.name().equalsIgnoreCase(state)) {
                return Optional.of(value);
            }
        }
        return Optional.empty();
    }
}
