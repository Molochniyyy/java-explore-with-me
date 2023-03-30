package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.EndpointHit;
import ru.practicum.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticRepository extends JpaRepository<EndpointHit, Long> {

    @Query("select new  ru.practicum.model.ViewStats(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHit as e " +
            "where e.timestamp between :start and :end " +
            "and e.uri in (:uris) " +
            "group by e.uri, e.app " +
            "order by count(e.ip) desc ")
    List<ViewStats> findStatWithUnique(@Param("uris") List<String> uris,
                                       @Param("start") LocalDateTime start,
                                       @Param("end") LocalDateTime end);

    @Query("select new  ru.practicum.model.ViewStats(e.app, e.uri, count(e.ip)) " +
            "from EndpointHit as e " +
            "where e.timestamp between :start and :end " +
            "and e.uri in (:uris) " +
            "group by e.uri, e.app " +
            "order by count(e.ip) desc ")
    List<ViewStats> findStatWithoutUnique(@Param("uris") List<String> uris,
                                          @Param("start") LocalDateTime start,
                                          @Param("end") LocalDateTime end);

    @Query("select new  ru.practicum.model.ViewStats(e.app, e.uri, count(e.ip)) " +
            "from EndpointHit as e " +
            "where e.timestamp between :start and :end " +
            "group by e.uri, e.app " +
            "order by count(e.ip) desc ")
    List<ViewStats> findWithoutUris(@Param("end") LocalDateTime end,
                                    @Param("start") LocalDateTime start);

    @Query("select new  ru.practicum.model.ViewStats(e.app, e.uri, count(distinct e.ip)) " +
            "from EndpointHit as e " +
            "where e.timestamp between :start and :end " +
            "group by e.uri, e.app " +
            "order by count(distinct e.ip) desc ")
    List<ViewStats> findWithoutUrisUnique(@Param("end") LocalDateTime end,
                                    @Param("start") LocalDateTime start);
}
