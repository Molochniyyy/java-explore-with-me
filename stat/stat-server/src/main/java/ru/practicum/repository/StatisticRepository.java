package ru.practicum.repository;

import ru.practicum.ViewStatsDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new ru.practicum.ViewStatsDto(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHit e " +
            "where e.timestamp between ?2 and ?3 " +
            "and e.uri in ?1 " +
            "group by e.app, e.uri")
    List<ViewStatsDto> findStatWithUnique(List<String> uris, LocalDateTime start, LocalDateTime end);

    @Query("select new ru.practicum.ViewStatsDto(e.app, e.uri, count(e.ip)) " +
            "from EndpointHit e " +
            "where e.timestamp between ?2 and ?3 " +
            "and e.uri in ?1 " +
            "group by e.app, e.uri")
    List<ViewStatsDto> findStatNOtUnique(List<String> uris, LocalDateTime start, LocalDateTime end);
}
