package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatRepository extends JpaRepository<EndpointHit, Integer> {
    @Query("select new ru.practicum.model.ViewStats(eh.app, eh.uri, count(distinct(eh.ip))) " +
            "from EndpointHit as eh " +
            "where eh.timestamp between :start and :end " +
            "and eh.uri in (:uris) " +
            "group by eh.uri, eh.app " +
            "order by count(distinct(eh.ip)) desc ")
    List<ViewStats> getAllBetweenStartAndEndUnique(@Param("start") LocalDateTime start,
                                                   @Param("end") LocalDateTime end,
                                                   @Param("uris") List<String> uris);

    @Query("select new ru.practicum.model.ViewStats(eh.app, eh.uri, count(eh.uri)) " +
            "from EndpointHit as eh " +
            "where eh.timestamp between :start and :end " +
            "and eh.uri in (:uris) " +
            "group by eh.uri, eh.app  " +
            "order by count(eh.uri) desc ")
    List<ViewStats> getAllBetweenStartAndEnd(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end,
                                             @Param("uris") List<String> uris);
}
