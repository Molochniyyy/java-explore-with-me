package ru.practicum.events.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.events.model.EventState;
import ru.practicum.events.model.Location;
import ru.practicum.users.dto.UserShortDto;
import ru.practicum.utils.Create;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFullDto {
    Long id;
    @NotNull(groups = {Create.class})
    String annotation;
    @NotNull(groups = {Create.class})
    CategoryDto category;
    Long confirmedRequests;
    LocalDateTime createdOn;
    String description;
    @NotNull(groups = {Create.class})
    LocalDateTime eventDate;
    UserShortDto initiator;
    @NotNull(groups = {Create.class})
    Location location;
    @NotNull(groups = {Create.class})
    Boolean paid;
    @PositiveOrZero(groups = {Create.class})
    Long participantLimit;
    LocalDateTime publishedOn;
    Boolean requestModeration;
    EventState state;
    @NotNull(groups = {Create.class})
    String title;
    Long views;
}
