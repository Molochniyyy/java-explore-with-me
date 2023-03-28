package ru.practicum.mapper;

import ru.practicum.ViewStatsDto;
import ru.practicum.model.ViewStats;

public class ViewStatsMapper {
    public static ViewStatsDto toDto(ViewStats stats) {
        return ViewStatsDto.builder()
                .app(stats.getApp())
                .hits(stats.getHits())
                .uri(stats.getUri())
                .build();
    }
}
