package ru.practicum.events.model;

import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class Location {
    Double lon;
    Double lat;
}
