package ru.practicum.mapper;

import ru.practicum.EndpointHitDto;
import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;
import ru.practicum.model.EndpointHit;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface StatisticMapper {
    StatisticMapper INSTANCE = Mappers.getMapper(StatisticMapper.class);

    EndpointHitDto toHitEndpointDto(EndpointHit hitEndpoint);

    EndpointHit toHitEndpoint(EndpointHitDto endpointHitDto);
}
