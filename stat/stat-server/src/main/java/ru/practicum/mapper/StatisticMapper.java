package ru.practicum.mapper;

import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class StatisticMapper {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static EndpointHit fromDto(EndpointHitDto hitDtoRequest) {
        EndpointHit hit = new EndpointHit();
        hit.setApp(hitDtoRequest.getApp());
        hit.setIp(hitDtoRequest.getIp());
        hit.setUri(hitDtoRequest.getUri());
        hit.setTimestamp(LocalDateTime.parse(hitDtoRequest.getTimestamp(), formatter));
        return hit;
    }
}
