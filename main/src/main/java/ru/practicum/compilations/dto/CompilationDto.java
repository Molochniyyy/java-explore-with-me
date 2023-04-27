package ru.practicum.compilations.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.utils.Create;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
public class CompilationDto {
    @NotNull(groups = {Create.class})
    Long id;
    List<EventShortDto> events;
    @NotNull(groups = {Create.class})
    String title;
    @NotNull(groups = {Create.class})
    boolean pinned;
}
