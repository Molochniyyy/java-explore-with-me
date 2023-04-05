package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.events.model.Event;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.ParticipationRequestStatus;
import ru.practicum.users.model.User;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @Query("select new ru.practicum.requests.dto.ParticipationRequestDto( " +
            "pr.id, pr.event.id, pr.created, pr.requesterId, pr.status) " +
            "from ParticipationRequest as pr " +
            "where pr.requesterId = :requesterId")
    List<ParticipationRequestDto> findAllByRequester(Long requesterId);

    Long countAllByEventAndStatus(Event event, ParticipationRequestStatus status);

}
