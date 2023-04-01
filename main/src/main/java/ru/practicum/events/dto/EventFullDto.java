package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.events.model.EventState;
import ru.practicum.events.model.Location;
import ru.practicum.users.dto.UserShortDto;

import java.time.LocalDateTime;

public class EventFullDto {
    Long id;
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;
    String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    UserShortDto initiator;
    Location location;
    Boolean paid;
    Integer participantLimit;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;
    Boolean requestModeration;
    EventState state;
    String title;
    Long views;
}
