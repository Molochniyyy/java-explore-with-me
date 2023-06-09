package ru.practicum.requests.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.ParticipationRequest;

import java.util.List;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = "spring")
public interface ParticipationRequestMapper {

    @Mapping(target = "requester", source = "requester.id")
    @Mapping(target = "event", source = "event.id")
    ParticipationRequestDto toDto(ParticipationRequest participationRequest);

    @Mapping(target = "requester", source = "requester.id")
    @Mapping(target = "event", source = "event.id")
    List<ParticipationRequestDto> toDtos(List<ParticipationRequest> participationRequest);


}