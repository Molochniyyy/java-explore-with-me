package ru.practicum.mapper;

import org.mapstruct.Mapper;
import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;

@Mapper(componentModel = "spring")
public interface StatisticMapper {

    EndpointHitDto toDto(EndpointHit hitEndpoint);

    EndpointHit fromDto(EndpointHitDto endpointHitDto);
}
