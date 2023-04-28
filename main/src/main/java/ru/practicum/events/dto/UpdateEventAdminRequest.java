package ru.practicum.events.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.events.model.Location;
import ru.practicum.events.model.StateAdminAction;
import ru.practicum.utils.Update;

import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEventAdminRequest {
    @Size(min = 20, max = 2000, groups = {Update.class})
    String annotation;
    Long categoryId;
    @Size(min = 20, max = 7000, groups = {Update.class})
    String description;
    LocalDateTime eventDate;
    Location location;
    Boolean paid;
    @PositiveOrZero(groups = {Update.class})
    Long participantLimit;
    Boolean requestModeration;
    StateAdminAction stateAction;
    @Size(min = 3, max = 120, groups = {Update.class})
    String title;
}
