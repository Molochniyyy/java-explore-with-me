package ru.practicum.compilations.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.compilations.model.Compilation;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.model.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "eventsOfCompilation", source = "events")
    Compilation toEntity(NewCompilationDto compilationNewDto, List<Event> events);

    @Mapping(target = "events", source = "eventsOfCompilation")
    CompilationDto toDto(Compilation compilation);

    @Mapping(target = "events", source = "events")
    CompilationDto toDto(Compilation compilation, List<EventShortDto> events);

}