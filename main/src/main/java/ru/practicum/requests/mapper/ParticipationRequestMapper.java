package ru.practicum.requests.mapper;

import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.ParticipationRequest;

import java.util.Collection;
import java.util.stream.Collectors;

public class ParticipationRequestMapper {
    public static ParticipationRequestDto toDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .requesterId(request.getRequesterId())
                .eventId(request.getEvent().getId())
                .created(request.getCreated())
                .status(request.getStatus())
                .build();
    }

    public static Collection<ParticipationRequestDto> toDto(Collection<ParticipationRequest> requests) {
        return requests.stream()
                .map(ParticipationRequestMapper::toDto)
                .collect(Collectors.toList());
    }
}
