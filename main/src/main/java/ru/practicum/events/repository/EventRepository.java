package ru.practicum.events.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.events.model.Event;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @EntityGraph(value = "event-entity-graph")
    List<Event> findAllByIdIn(List<Long> ids);

}
