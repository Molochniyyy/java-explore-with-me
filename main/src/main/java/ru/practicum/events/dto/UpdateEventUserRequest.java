package ru.practicum.events.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.model.Location;
import ru.practicum.events.model.StateUserAction;
import ru.practicum.utils.Update;

import javax.validation.constraints.Future;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequest {
    @Size(min = 20, max = 2000, groups = {Update.class})
    String annotation;
    Long category;
    @Size(min = 20, max = 7000, groups = {Update.class})
    String description;
    @Future(groups = {Update.class})
    LocalDateTime eventDate;
    Location location;
    Boolean paid;
    @PositiveOrZero(groups = {Update.class})
    Long participantLimit;
    Boolean requestModeration;
    StateUserAction stateAction;
    @Size(min = 3, max = 120, groups = {Update.class})
    String title;
}
