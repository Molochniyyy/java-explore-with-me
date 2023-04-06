package ru.practicum.events.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.model.Location;
import ru.practicum.utils.Create;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @Size(min = 20, max = 2000)
    @NotNull(groups = {Create.class})
    String annotation;
    @NotNull(groups = {Create.class})
    Long categoryId;
    @Size(min = 20, max = 7000)
    @NotNull(groups = {Create.class})
    String description;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @NotNull(groups = {Create.class})
    LocalDateTime eventDate;
    @NotNull(groups = {Create.class})
    Location location;
    boolean paid;
    @PositiveOrZero(groups = {Create.class})
    Long participantLimit;
    boolean requestModeration;
    @Size(min = 3, max = 120)
    @NotNull(groups = {Create.class})
    String title;
}
