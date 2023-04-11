package ru.practicum.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.events.model.Event;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.requests.model.ParticipationRequest;
import ru.practicum.requests.model.ParticipationRequestStatus;
import ru.practicum.users.model.User;

import java.util.List;

@Repository
public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    @Query("select new ru.practicum.requests.dto.ParticipationRequestDto( " +
            "pr.id, pr.event.id, pr.created, pr.requester.id, pr.status) " +
            "from ParticipationRequest as pr " +
            "where pr.requester = :requester")
    List<ParticipationRequestDto> findAllByRequester(User requester);

    Long countAllByEventAndStatus(Event event, ParticipationRequestStatus status);

}
