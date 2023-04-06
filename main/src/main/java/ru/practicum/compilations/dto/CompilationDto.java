package ru.practicum.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.utils.Create;

import javax.validation.constraints.NotNull;
import java.util.Collection;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    @NotNull(groups = {Create.class})
    Long id;
    Collection<EventShortDto> events;
    @NotNull(groups = {Create.class})
    String title;
    @NotNull(groups = {Create.class})
    boolean pinned;
}
