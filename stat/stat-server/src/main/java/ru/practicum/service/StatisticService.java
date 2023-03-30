package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.mapper.StatisticMapper;
import ru.practicum.mapper.ViewStatsMapper;
import ru.practicum.repository.StatisticRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final StatisticRepository statisticRepository;
    private final StatisticMapper statisticMapper = Mappers.getMapper(StatisticMapper.class);
    private final ViewStatsMapper viewStatsMapper = Mappers.getMapper(ViewStatsMapper.class);

    public EndpointHitDto saveStatistic(EndpointHitDto endpointHitDto) {
        return statisticMapper.toDto(statisticRepository.save(statisticMapper.fromDto(endpointHitDto)));
    }

    public List<ViewStatsDto> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                return statisticRepository.findWithoutUrisUnique(end, start).stream()
                        .map(viewStatsMapper::toDto).collect(Collectors.toList());
            } else {
                return statisticRepository.findWithoutUris(end, start).stream()
                        .map(viewStatsMapper::toDto).collect(Collectors.toList());
            }
        }
        if (unique) {
            return statisticRepository.findStatWithUnique(uris, start, end).stream()
                    .map(viewStatsMapper::toDto).collect(Collectors.toList());
        } else {
            return statisticRepository.findStatWithoutUnique(uris, start, end).stream()
                    .map(viewStatsMapper::toDto).collect(Collectors.toList());
        }
    }
}

