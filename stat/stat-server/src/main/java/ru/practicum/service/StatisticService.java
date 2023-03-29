package ru.practicum.service;

import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.mapper.StatisticMapper;
import ru.practicum.repository.StatisticRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticService {
    private final StatisticRepository statisticRepository;

    public EndpointHitDto saveStatistic(EndpointHitDto endpointHitDto) {
        return StatisticMapper.INSTANCE.toHitEndpointDto(
                statisticRepository.save(StatisticMapper.INSTANCE.toHitEndpoint(endpointHitDto)));
    }

    public List<ViewStatsDto> getStatistic(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris == null || uris.isEmpty()) {
            return new ArrayList<>();
        }
        if (unique) {
            return statisticRepository.findStatWithUnique(uris, start, end)
                    .stream()
                    .sorted(Comparator.comparing(ViewStatsDto::getHits).reversed()).collect(Collectors.toList());
        } else {
            return statisticRepository.findStatNOtUnique(uris, start, end)
                    .stream()
                    .sorted(Comparator.comparing(ViewStatsDto::getHits).reversed()).collect(Collectors.toList());
        }
    }
}

