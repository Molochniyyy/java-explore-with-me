package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStatsDto;
import ru.practicum.mapper.EndpointMapper;
import ru.practicum.mapper.ViewStatsMapper;
import ru.practicum.model.ViewStats;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatServiceImpl implements StatService {

    private final StatRepository repository;

    @Override
    public EndpointHitDto saveHit(EndpointHitDto hitDto) {
        return EndpointMapper.toDto(repository.save(EndpointMapper.fromDto(hitDto)));
    }

    @Override
    public List<ViewStatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        List<ViewStats> stats;
        if (uris.isEmpty()) {
            stats = Collections.emptyList();
        }
        if (unique) {
            stats = repository.getAllBetweenStartAndEndUnique(start, end, uris);
        } else {
            stats = repository.getAllBetweenStartAndEnd(start, end, uris);
        }
        return stats.stream()
                .map(ViewStatsMapper::toDto)
                .collect(Collectors.toList());
    }
}
