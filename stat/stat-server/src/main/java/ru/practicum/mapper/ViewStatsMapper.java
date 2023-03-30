package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ViewStatsDto;
import ru.practicum.model.ViewStats;

@Mapper(componentModel = "spring")
public interface ViewStatsMapper {
    ViewStatsDto toDto(ViewStats stats);

    ViewStats fromDto(ViewStatsDto statsDto);
}
