package ru.practicum.events.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import javax.persistence.Embeddable;

@Embeddable
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Location {
    Double lon;
    Double lat;
}
