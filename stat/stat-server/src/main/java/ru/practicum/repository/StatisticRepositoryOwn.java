package ru.practicum.repository;

import ru.practicum.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepositoryOwn {
    List<ViewStatsDto> findAllWithHits(LocalDateTime startTime, LocalDateTime endTime,
                                       List<String> uris, Boolean unique);
}
