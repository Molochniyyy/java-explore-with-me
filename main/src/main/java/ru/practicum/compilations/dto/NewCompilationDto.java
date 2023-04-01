package ru.practicum.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.model.Event;

import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCompilationDto {
    Collection<Event> events;
    boolean pinned;
    String title;
}
